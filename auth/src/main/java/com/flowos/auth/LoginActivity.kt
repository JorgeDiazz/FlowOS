package com.flowos.auth

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
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
    initializeSubscription()
    initializeSensorsSubscription()
  }

  private fun setUpMotionDetector() {
    val accelerometerSensor = motionSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    motionSensorManager.registerListener(motionSensorListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL)
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
      is LoginNews.LoginSuccessful -> startWelcomeActivity(news.driverData)
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

  private fun setUpView() {
    binding.apply {
      buttonContinue.setOnClickListener {
        val driverId = textFieldDriverId.editText?.text.toString()
        val boardId = textFieldRunningBoardId.editText?.text.toString()
        viewModel.loginUser(driverId, boardId)
      }
    }
  }

  private fun isAccelerometerMeasuresIntervalAvailable(currentTime: Long): Boolean {
    val elapsedTimeInSeconds = (currentTime - lastTimeAccelerometerMeasuresProcessed) / 1000
    return elapsedTimeInSeconds > ACCELEROMETER_MEASURES_PROCESSING_INTERVAL_IN_SECONDS
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
}
