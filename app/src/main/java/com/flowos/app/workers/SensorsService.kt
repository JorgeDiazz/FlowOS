package com.flowos.app.workers

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorManager
import android.location.LocationListener
import android.location.LocationManager
import android.os.BatteryManager
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.flowos.app.R
import com.flowos.app.SplashActivity
import com.flowos.base.interfaces.Logger
import com.flowos.base.others.FIVE_SECONDS_IN_MILLISECONDS
import com.flowos.base.others.LOG_OFF_INTENT_FILTER
import com.flowos.base.others.ONE_MINUTE_IN_MILLISECONDS
import com.flowos.base.others.THIRTY_SECONDS_IN_MILLISECONDS
import com.flowos.base.others.TURN_SCREEN_INTENT_FILTER
import com.flowos.base.others.TURN_SCREEN_ON
import com.flowos.components.utils.getBatteryLevel
import com.flowos.components.utils.isConnectedToPower
import com.flowos.sensors.data.SensorsNews
import com.flowos.sensors.listeners.MotionSensorListener
import com.flowos.sensors.viewModels.SensorsViewModel
import dagger.android.AndroidInjection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

private const val LOCATION_TRIGGER_DISTANCE_IN_METERS: Long = 1
private const val LOCATION_TRIGGER_GET_DATA_INTERVAL_DURATION_IN_SECONDS: Float = FIVE_SECONDS_IN_MILLISECONDS.toFloat()

private val MOTION_SENSORS = arrayOf(Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_LINEAR_ACCELERATION, Sensor.TYPE_GYROSCOPE)

private const val BLUETOOTH_LOW_ENERGY_SCAN_INTERVAL_IN_SECONDS = THIRTY_SECONDS_IN_MILLISECONDS

private const val FOREGROUND_SENSORS_SERVICE_CHANNEL_ID = "ForegroundSensorsServiceChannel"

private const val ACCELEROMETER_MEASURES_PROCESSING_INTERVAL_IN_SECONDS = 15

/**
 * Represents sensors' work
 *
 * This is the orchestrator of sensors functionality (GPS, BLE, NFC, -linear- accelerometer, gyroscope).
 */
class SensorsService : Service() {

  @Inject
  lateinit var logger: Logger

  @Inject
  lateinit var sensorsViewModel: SensorsViewModel

  @Inject
  lateinit var appContext: Context

  private val locationManager by lazy { appContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager }

  private val locationListener = LocationListener {
    sensorsViewModel.sendDeviceLocationUpdate(it)
  }

  private val motionSensorManager by lazy { appContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager }

  private var lastTimeAccelerometerMeasuresProcessed: Long = Calendar.getInstance().time.time

  private val motionSensorListener = MotionSensorListener {
    sensorsViewModel.cacheSensorMeasure(it)

    val currentTime: Long = Calendar.getInstance().time.time
    val accelerometerMeasuresIntervalAvailable = isAccelerometerMeasuresIntervalAvailable(currentTime)

    if (accelerometerMeasuresIntervalAvailable && isConnectedToPower()) {
      lastTimeAccelerometerMeasuresProcessed = currentTime
      sensorsViewModel.detectMovement(it)
    }
  }

  private val oldBleDevices = LinkedHashSet<String>()
  private val newBleDevices = LinkedHashSet<String>()

  private val bleScannerCallback: ScanCallback = object : ScanCallback() {
    override fun onScanResult(callbackType: Int, result: ScanResult) {
      newBleDevices.add(result.device.address)
    }
  }

  private var logOffJob: Job? = null

  private var sensorsEnabled = false

  private val batteryChangedReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      val currentTimestamp = System.currentTimeMillis() / 1000

      val devicePlugged = when (intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)) {
        BatteryManager.BATTERY_PLUGGED_AC, BatteryManager.BATTERY_PLUGGED_USB, BatteryManager.BATTERY_PLUGGED_WIRELESS -> true
        else -> false
      }

      sensorsViewModel.sendDeviceStateUpdate(currentTimestamp, devicePlugged, getBatteryLevel())

      if (devicePlugged && !sensorsEnabled) {
        initializeSensors()
        logOffJob?.cancel()
      } else if (!devicePlugged) {
        unregisterSensorManagers()
        logOffUserAfter(ONE_MINUTE_IN_MILLISECONDS)
      }
    }
  }

  private fun logOffUserAfter(milliseconds: Long) {
    logOffJob = CoroutineScope(Dispatchers.Main.immediate).launch {
      delay(milliseconds)
      sendLogOffBroadcast()
    }
  }

  private fun sendLogOffBroadcast() {
    LocalBroadcastManager.getInstance(appContext).sendBroadcast(
      Intent(LOG_OFF_INTENT_FILTER)
    )
  }

  override fun onCreate() {
    AndroidInjection.inject(this)
    super.onCreate()

    initializeSubscription()

    initializeBatteryChangedReceiver()
    initializeSensors()
  }

  private fun isAccelerometerMeasuresIntervalAvailable(currentTime: Long): Boolean {
    val elapsedTimeInSeconds = (currentTime - lastTimeAccelerometerMeasuresProcessed) / 1000
    return elapsedTimeInSeconds > ACCELEROMETER_MEASURES_PROCESSING_INTERVAL_IN_SECONDS
  }

  private fun initializeBatteryChangedReceiver() {
    appContext.registerReceiver(batteryChangedReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
  }

  private fun initializeSubscription() {
    sensorsViewModel.news.observeForever {
      handleNews(it.peekContent())
    }
  }

  private fun handleNews(news: SensorsNews) {
    when (news) {
      SensorsNews.LocationUpdatePublished -> Toast.makeText(
        appContext,
        R.string.location_update_published_message,
        Toast.LENGTH_LONG
      ).show()

      is SensorsNews.ShowErrorNews -> Toast.makeText(
        appContext,
        news.errorMessage,
        Toast.LENGTH_LONG
      ).show()

      SensorsNews.NoDeviceMovement -> {
        logger.d("Sending turnScreenOn broadcast...")
        sendTurnScreenBroadcast(true)
      }
    }
  }

  private fun sendTurnScreenBroadcast(turnScreenOn: Boolean) {
    LocalBroadcastManager.getInstance(appContext).sendBroadcast(
      Intent(TURN_SCREEN_INTENT_FILTER).putExtra(TURN_SCREEN_ON, turnScreenOn)
    )
  }

  private fun initializeSensors() {
    sensorsEnabled = true

    setUpMotionDetector()
    initializeLocationSensor()
    initializeMotionSensors()
    initializeBluetoothLowEnergySensor()
  }

  private fun setUpMotionDetector() {
    val accelerometerSensor = motionSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    motionSensorManager.registerListener(motionSensorListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
  }

  private fun initializeLocationSensor() {
    setUpLocationManager()
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

  private fun initializeMotionSensors() {
    MOTION_SENSORS.map { motionSensorManager.getDefaultSensor(it) }.forEach {
      motionSensorManager.registerListener(motionSensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
    }
  }

  private fun initializeBluetoothLowEnergySensor() {
    setUpBluetoothLowEnergy()
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

      scheduleBluetoothLowEnergyScanProcess(bleScanner, bleScannerSettings)
    }
  }

  private fun updateBleDevicesLists() {
    val currentTimestamp = System.currentTimeMillis() / 1000
    val bleDevicesAdded = newBleDevices.filterNot { oldBleDevices.contains(it) }
    val bleDevicesRemoved = oldBleDevices.filterNot { newBleDevices.contains(it) }

    sensorsViewModel.onBleUpdate(currentTimestamp, bleDevicesAdded, bleDevicesRemoved)

    logger.d("BLE devices discovered: $newBleDevices")
    logger.d("BLE devices added: $bleDevicesAdded")
    logger.d("BLE devices removed: $bleDevicesRemoved")

    oldBleDevices.clear()
    oldBleDevices.addAll(newBleDevices)
    newBleDevices.clear()
  }

  private fun unregisterSensorManagers() {
    sensorsEnabled = false

    locationManager.removeUpdates(locationListener)
    motionSensorManager.unregisterListener(motionSensorListener)
  }

  override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
    createNotificationChannel()

    val notificationIntent = Intent(this, SplashActivity::class.java)
    val pendingIntent = PendingIntent.getActivity(
      this,
      0, notificationIntent, 0
    )

    val notification: Notification = NotificationCompat.Builder(this, FOREGROUND_SENSORS_SERVICE_CHANNEL_ID)
      .setContentTitle("Sensors Foreground Service")
      .setContentIntent(pendingIntent)
      .build()

    startForeground(1, notification)

    return START_STICKY
  }

  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val serviceChannel = NotificationChannel(
        FOREGROUND_SENSORS_SERVICE_CHANNEL_ID,
        "Sensors Foreground Service Channel",
        NotificationManager.IMPORTANCE_HIGH
      )

      val manager = getSystemService(NotificationManager::class.java)
      manager.createNotificationChannel(serviceChannel)
    }
  }

  override fun onBind(intent: Intent?): IBinder? = null
}
