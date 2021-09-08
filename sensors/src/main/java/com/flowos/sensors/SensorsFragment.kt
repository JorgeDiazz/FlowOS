package com.flowos.sensors

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.BatteryManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.flowos.base.interfaces.Logger
import com.flowos.base.others.FIVE_SECONDS_IN_MILLISECONDS
import com.flowos.base.others.THIRTY_SECONDS_IN_MILLISECONDS
import com.flowos.components.utils.viewBinding
import com.flowos.core.EventObserver
import com.flowos.sensors.data.SensorsNews
import com.flowos.sensors.data.SensorsUiModel
import com.flowos.sensors.databinding.FragmentSensorsBinding
import com.flowos.sensors.listeners.MotionSensorListener
import com.flowos.sensors.viewModels.SensorsViewModel
import dagger.android.support.AndroidSupportInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val LOCATION_TRIGGER_DISTANCE_IN_METERS: Long = 1
private const val LOCATION_TRIGGER_GET_DATA_INTERVAL_DURATION_IN_SECONDS: Float = FIVE_SECONDS_IN_MILLISECONDS.toFloat()

private const val BLUETOOTH_LOW_ENERGY_SCAN_INTERVAL_IN_SECONDS = THIRTY_SECONDS_IN_MILLISECONDS

private val MOTION_SENSORS = arrayOf(Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_LINEAR_ACCELERATION, Sensor.TYPE_GYROSCOPE)

/**
 * Represents sensors fragment.
 *
 * This is the orchestrator of sensors functionality (GPS, BLE, NFC, -linear- accelerometer, gyroscope).
 */
class SensorsFragment : Fragment(R.layout.fragment_sensors) {

  companion object {
    fun newInstance(): SensorsFragment = SensorsFragment()
  }

  private val gpsRequestPermissionLauncher =
    registerForActivityResult(
      ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
      if (isGranted) {
        setUpLocationManager()
      } else {
        showPermissionRequiredMessage()
        initializeLocationSensor()
      }
    }

  private val bleRequestPermissionLauncher =
    registerForActivityResult(
      ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
      if (isGranted) {
        setUpBluetoothLowEnergy()
      } else {
        showPermissionRequiredMessage()
        initializeBluetoothLowEnergySensor()
      }
    }

  private val locationListener = LocationListener {
    viewModel.sendDeviceLocationUpdate(it)
  }

  private val oldBleDevices = LinkedHashSet<String>()
  private val newBleDevices = LinkedHashSet<String>()

  private val bleScannerCallback: ScanCallback = object : ScanCallback() {
    override fun onScanResult(callbackType: Int, result: ScanResult) {
      newBleDevices.add(result.device.address)
    }

    override fun onBatchScanResults(results: List<ScanResult?>?) {
      logger.d("BLE Batch Scan Results received! $results")
    }

    override fun onScanFailed(errorCode: Int) {
      logger.e("BLE scan failed! Error code: $errorCode")
    }
  }

  @Inject
  lateinit var logger: Logger

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  private val viewModel: SensorsViewModel by viewModels { viewModelFactory }

  private val binding by viewBinding(FragmentSensorsBinding::bind)

  private val locationManager by lazy { requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager }

  private val motionSensorManager by lazy { requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager }

  private val motionSensorListener = MotionSensorListener { viewModel.cacheSensorMeasure(it) }

  private val batteryChangedReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      when (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
        BatteryManager.BATTERY_PLUGGED_AC, BatteryManager.BATTERY_PLUGGED_USB, BatteryManager.BATTERY_PLUGGED_WIRELESS -> initializeSensors()
        else -> unregisterSensorManagers()
      }
    }
  }

  override fun onAttach(context: Context) {
    AndroidSupportInjection.inject(this)
    super.onAttach(context)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    initializeViewModel()
    initializeObserver()
    initializeSubscription()
    initializeBatteryChangedReceiver()
  }

  private fun initializeViewModel() {
    viewModel.onViewActive()
  }

  private fun initializeObserver() {
    viewModel.liveData.observe(
      viewLifecycleOwner,
      {
        observeData(it)
      }
    )
  }

  private fun initializeSubscription() {
    viewModel.news.observe(
      viewLifecycleOwner,
      EventObserver {
        handleNews(it)
      }
    )
  }

  private fun initializeBatteryChangedReceiver() {
    requireActivity().registerReceiver(batteryChangedReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
  }

  private fun initializeSensors() {
    initializeLocationSensor()
    initializeMotionSensors()
    initializeBluetoothLowEnergySensor()
  }

  private fun initializeLocationSensor() {
    enableGps()
  }

  private fun initializeMotionSensors() {
    MOTION_SENSORS.map { motionSensorManager.getDefaultSensor(it) }.forEach {
      motionSensorManager.registerListener(motionSensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
    }
  }

  private fun initializeBluetoothLowEnergySensor() {
    enableBluetoothLowEnergy()
  }

  private fun enableBluetoothLowEnergy() {
    when (PackageManager.PERMISSION_GRANTED) {
      ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH_ADMIN) ->
        setUpBluetoothLowEnergy()
      else -> bleRequestPermissionLauncher.launch(Manifest.permission.BLUETOOTH_ADMIN)
    }
  }

  private fun setUpBluetoothLowEnergy() {
    BluetoothAdapter.getDefaultAdapter()?.let { bleAdapter ->
      bleAdapter.bluetoothLeScanner?.let { bleScanner ->
        val bleScannerSettings = ScanSettings.Builder()
          .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER)
          .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
          .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE)
          .setNumOfMatches(ScanSettings.MATCH_NUM_ONE_ADVERTISEMENT)
          .setReportDelay(0L)
          .build()

        scheduleBluetoothLowEnergyScanProcess(bleScanner, bleScannerSettings)
      } ?: kotlin.run {
        logger.e("Error getting BLE Scanner")
      }
    }
  }

  @SuppressLint("MissingPermission")
  private fun scheduleBluetoothLowEnergyScanProcess(bleScanner: BluetoothLeScanner, bleScannerSettings: ScanSettings?) {
    CoroutineScope(Dispatchers.Main.immediate).launch {
      bleScanner.startScan(null, bleScannerSettings, bleScannerCallback)
      logger.d("BLE Scanner started!")

      delay(BLUETOOTH_LOW_ENERGY_SCAN_INTERVAL_IN_SECONDS)
      bleScanner.stopScan(bleScannerCallback)
      logger.d("BLE Scanner stopped!")

      updateBleDevicesLists()
      delay(BLUETOOTH_LOW_ENERGY_SCAN_INTERVAL_IN_SECONDS / 2)
    }

    scheduleBluetoothLowEnergyScanProcess(bleScanner, bleScannerSettings)
  }

  private fun updateBleDevicesLists() {
    logger.d("BLE devices discovered: $newBleDevices")
    logger.d("BLE devices added: ${newBleDevices.filterNot { oldBleDevices.contains(it) }}")
    logger.d("BLE devices removed: ${oldBleDevices.filterNot { newBleDevices.contains(it) }}")

    oldBleDevices.clear()
    oldBleDevices.addAll(newBleDevices)
    newBleDevices.clear()
  }

  private fun enableGps() {
    when (PackageManager.PERMISSION_GRANTED) {
      ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ->
        setUpLocationManager()
      else -> gpsRequestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
  }

  private fun setUpLocationManager() {
    val gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    if (gpsEnabled) {
      logger.d("GPS is enabled!")
      requestLocationUpdates(locationManager)
    } else {
      logger.e("GPS is NOT enabled!")
    }
  }

  @SuppressLint("MissingPermission")
  private fun requestLocationUpdates(locationManager: LocationManager) {
    locationManager.requestLocationUpdates(
      LocationManager.GPS_PROVIDER,
      LOCATION_TRIGGER_DISTANCE_IN_METERS,
      LOCATION_TRIGGER_GET_DATA_INTERVAL_DURATION_IN_SECONDS,
      locationListener
    )
  }

  private fun showPermissionRequiredMessage() {
    Toast.makeText(
      requireContext(),
      R.string.permission_required_message,
      Toast.LENGTH_LONG
    ).show()
  }

  private fun observeData(uiModel: SensorsUiModel) {
    uiModel.vehicleId?.let {
      binding.textViewVehicleIdValue.text = it
    }
  }

  private fun handleNews(news: SensorsNews) {
    when (news) {
      SensorsNews.LocationUpdatePublished -> Toast.makeText(
        requireContext(),
        R.string.location_update_published_message,
        Toast.LENGTH_SHORT
      ).show()
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    unregisterSensorManagers()
  }

  private fun unregisterSensorManagers() {
    motionSensorManager.unregisterListener(motionSensorListener)
    locationManager.removeUpdates(locationListener)
  }
}
