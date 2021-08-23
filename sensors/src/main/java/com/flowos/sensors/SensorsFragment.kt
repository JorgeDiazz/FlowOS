package com.flowos.sensors

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.LocationListener
import android.location.LocationManager
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
import com.flowos.components.utils.viewBinding
import com.flowos.core.EventObserver
import com.flowos.sensors.data.SensorsNews
import com.flowos.sensors.data.SensorsUiModel
import com.flowos.sensors.databinding.FragmentSensorsBinding
import com.flowos.sensors.listeners.MotionSensorListener
import com.flowos.sensors.viewModels.SensorsViewModel
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject

private const val LOCATION_TRIGGER_DISTANCE_IN_METERS: Long = 1
private const val LOCATION_TRIGGER_GET_DATA_INTERVAL_DURATION_IN_SECONDS: Float = FIVE_SECONDS_IN_MILLISECONDS.toFloat()

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

  private val locationListener = LocationListener {
    viewModel.sendDeviceLocationUpdate(it)
  }

  @Inject
  lateinit var logger: Logger

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  private val viewModel: SensorsViewModel by viewModels { viewModelFactory }

  private val binding by viewBinding(FragmentSensorsBinding::bind)

  private val locationSensorManager by lazy { requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager }

  private val motionSensorManager by lazy { requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager }

  private val motionSensorListener = MotionSensorListener { viewModel.cacheSensorMeasure(it) }

  override fun onAttach(context: Context) {
    AndroidSupportInjection.inject(this)
    super.onAttach(context)
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    initializeObserver()
    initializeSubscription()
    initializeSensors()
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

  private fun initializeSensors() {
    initializeLocationSensor()
    initializeMotionSensors()
  }

  private fun initializeLocationSensor() {
    enableGps()
  }

  private fun initializeMotionSensors() {
    MOTION_SENSORS.map { motionSensorManager.getDefaultSensor(it) }.forEach {
      motionSensorManager.registerListener(motionSensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
    }
  }

  private fun enableGps() {
    when (PackageManager.PERMISSION_GRANTED) {
      ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) ->
        setUpLocationManager()
      else -> gpsRequestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }
  }

  private fun setUpLocationManager() {
    val gpsEnabled = locationSensorManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

    if (gpsEnabled) {
      logger.d("GPS is enabled!")
      requestLocationUpdates(locationSensorManager)
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
  }
}
