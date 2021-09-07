package com.flowos.auth

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.flowos.auth.WelcomeActivity.Companion.DRIVER_DATA_KEY
import com.flowos.auth.data.LoginNews
import com.flowos.auth.data.parcelize
import com.flowos.auth.databinding.ActivityLoginBinding
import com.flowos.auth.domain.data.DriverData
import com.flowos.auth.viewModels.LoginViewModel
import com.flowos.base.interfaces.Logger
import com.flowos.components.utils.isConnectedToPower
import com.flowos.components.utils.makeErrorSnackbar
import com.flowos.components.utils.turnScreenOn
import com.flowos.components.utils.viewBinding
import com.flowos.core.Event
import com.flowos.core.EventObserver
import com.flowos.sensors.data.SensorsNews
import com.flowos.sensors.data.SensorsUiModel
import com.flowos.sensors.listeners.MotionSensorListener
import com.flowos.sensors.viewModels.SensorsViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import java.util.Calendar
import javax.inject.Inject

private const val ACCELEROMETER_MEASURES_PROCESSING_INTERVAL_IN_SECONDS = 5

/**
 * Represents login activity.
 *
 * This is the interface to log-in users.
 */
class LoginActivity : AppCompatActivity() {

  @Inject
  lateinit var logger: Logger

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  @Inject
  lateinit var sensorsViewModel: SensorsViewModel

  private lateinit var sensorsEventObserver: Observer<Event<SensorsNews>>

  private val viewModel: LoginViewModel by viewModels { viewModelFactory }

  private val binding by viewBinding(ActivityLoginBinding::inflate)

  private var nfcAdapter: NfcAdapter? = null

  private var indefiniteErrorSnackbar: Snackbar? = null

  private val motionSensorManager by lazy { getSystemService(Context.SENSOR_SERVICE) as SensorManager }

  private var lastTimeAccelerometerMeasuresProcessed: Long = Calendar.getInstance().time.time

  private val motionSensorListener = MotionSensorListener {
    val currentTime: Long = Calendar.getInstance().time.time
    val accelerometerMeasuresIntervalAvailable = isAccelerometerMeasuresIntervalAvailable(currentTime)

    if (accelerometerMeasuresIntervalAvailable && isConnectedToPower()) {
      lastTimeAccelerometerMeasuresProcessed = currentTime
      sensorsViewModel.detectMovement(it)
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)

    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    setUpView()
    setUpMotionDetector()
    initializeSensorsObserver()
    initializeSubscription()
    initializeSensorsSubscription()
  }

  private fun setUpView() {
    binding.apply {
      buttonContinue.setOnClickListener {
        val driverId = textFieldDriverId.editText?.text.toString()
        val boardId = textFieldRunningBoardId.editText?.text.toString()
        viewModel.loginUser(driverId, boardId)
      }
    }

    showPlaceInDeviceErrorSnackbar()
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    resolveIntent(intent)
  }

  private fun resolveIntent(intent: Intent) {
    when (intent.action) {
      NfcAdapter.ACTION_TAG_DISCOVERED,
      NfcAdapter.ACTION_TECH_DISCOVERED,
      NfcAdapter.ACTION_NDEF_DISCOVERED -> {
        resolveNfcIntent(intent)
        hidePlaceInDeviceErrorSnackbar()
      }
    }
  }

  private fun resolveNfcIntent(intent: Intent) {
    val extraTag: Tag? = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG) as Tag?
    val ndefMessages = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES)

    sensorsViewModel.getNfcPayload(extraTag, ndefMessages)
  }

  private fun showPlaceInDeviceErrorSnackbar() {
    indefiniteErrorSnackbar = makeErrorSnackbar(
      binding.root,
      getString(R.string.place_device_in_vehicle_to_start),
      Snackbar.LENGTH_INDEFINITE
    ).apply { show() }

    binding.buttonContinue.isEnabled = false
  }

  private fun hidePlaceInDeviceErrorSnackbar() {
    indefiniteErrorSnackbar?.dismiss()
    binding.buttonContinue.isEnabled = true
  }

  private fun setUpMotionDetector() {
    val accelerometerSensor = motionSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    motionSensorManager.registerListener(motionSensorListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
  }

  private fun initializeSensorsObserver() {
    sensorsViewModel.liveData.observeForever {
      observeSensorsData(it)
    }
  }

  private fun observeSensorsData(uiModel: SensorsUiModel) {
    uiModel.nfcPayload?.let {
      sensorsViewModel.cacheNfcPayload(it)

      Toast.makeText(this, "Vehicle id: $it", Toast.LENGTH_LONG).show()
    }
  }

  private fun initializeSensorsSubscription() {
    sensorsEventObserver = EventObserver { handleSensorsNews(it) }
    sensorsViewModel.news.observeForever(sensorsEventObserver)
  }

  private fun handleSensorsNews(news: SensorsNews) {
    when (news) {
      SensorsNews.NoDeviceMovement -> {
        logger.d("Attempt to turn screen on...")
        turnScreenOn()
      }
      else -> {
        // no-op by default
      }
    }
  }

  private fun initializeSubscription() {
    viewModel.news.observe(this, EventObserver { handleNews(it) })
  }

  private fun handleNews(news: LoginNews) {
    when (news) {
      is LoginNews.LoginSuccessful -> {
        disableNfcForegroundDispatch()
        startWelcomeActivity(news.driverData)
      }
      is LoginNews.ShowErrorNews -> makeErrorSnackbar(
        binding.root,
        news.message,
        Snackbar.LENGTH_LONG
      ).show()
    }
  }

  private fun startWelcomeActivity(driverData: DriverData) {
    Intent(this, WelcomeActivity::class.java).apply {
      putExtra(DRIVER_DATA_KEY, driverData.parcelize())
      startActivity(this)
      finish()
    }
  }

  private fun isAccelerometerMeasuresIntervalAvailable(currentTime: Long): Boolean {
    val elapsedTimeInSeconds = (currentTime - lastTimeAccelerometerMeasuresProcessed) / 1000
    return elapsedTimeInSeconds > ACCELEROMETER_MEASURES_PROCESSING_INTERVAL_IN_SECONDS
  }

  override fun onResume() {
    super.onResume()
    setUpNfcSensor()
  }

  private fun setUpNfcSensor() {
    nfcAdapter = NfcAdapter.getDefaultAdapter(this)

    val nfcPendingIntent = PendingIntent.getActivity(
      this, 0,
      Intent(this, this.javaClass)
        .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
      0
    )

    nfcAdapter?.enableForegroundDispatch(this, nfcPendingIntent, null, null)
  }

  override fun onDestroy() {
    super.onDestroy()

    unregisterSensorManagers()
    removeSensorsEventObserver()
  }

  private fun unregisterSensorManagers() {
    motionSensorManager.unregisterListener(motionSensorListener)
  }

  private fun removeSensorsEventObserver() {
    sensorsViewModel.news.removeObserver(sensorsEventObserver)
  }

  private fun disableNfcForegroundDispatch() {
    nfcAdapter?.disableForegroundDispatch(this)
  }

  override fun onBackPressed() {
    // no-op by default
  }
}
