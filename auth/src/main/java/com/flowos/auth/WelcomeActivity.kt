package com.flowos.auth

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.flowos.auth.data.DriverDataArgument
import com.flowos.auth.databinding.ActivityWelcomeBinding
import com.flowos.base.others.FIVE_SECONDS_IN_MILLISECONDS
import com.flowos.components.utils.viewBinding
import com.flowos.core.di.OpenMain
import dagger.android.AndroidInjection
import javax.inject.Inject

/**
 * Represents welcome activity.
 *
 * This is the screen where users are welcomed.
 */
class WelcomeActivity : AppCompatActivity() {

  companion object {
    const val DRIVER_DATA_KEY = "DRIVER_DATA_KEY"
  }

  @Inject
  @OpenMain
  @JvmSuppressWildcards
  lateinit var openMainAction: () -> Intent

  private val binding by viewBinding(ActivityWelcomeBinding::inflate)

  private val driverData: DriverDataArgument?
    get() = intent.extras?.getParcelable(DRIVER_DATA_KEY)

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)

    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    setUpView()
  }

  private fun setUpView() {
    setUpDriverNameLabel()
    closeActivityDelayed()
  }

  private fun setUpDriverNameLabel() {
    binding.textViewDriverName.text = driverData?.driverName
  }

  private fun closeActivityDelayed() {
    Handler(Looper.getMainLooper()).postDelayed(
      {
        startMainActivity()
      },
      FIVE_SECONDS_IN_MILLISECONDS
    )
  }

  private fun startMainActivity() {
    startActivity(openMainAction())
    finish()
  }
}
