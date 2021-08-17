package com.flowos.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.flowos.auth.WelcomeActivity.Companion.DRIVER_DATA_KEY
import com.flowos.auth.data.LoginNews
import com.flowos.auth.data.parcelize
import com.flowos.auth.databinding.ActivityLoginBinding
import com.flowos.auth.domain.data.DriverData
import com.flowos.auth.viewModels.LoginViewModel
import com.flowos.base.interfaces.Logger
import com.flowos.components.utils.makeErrorSnackbar
import com.flowos.components.utils.viewBinding
import com.flowos.core.EventObserver
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import javax.inject.Inject

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

  private val viewModel: LoginViewModel by viewModels { viewModelFactory }

  private val binding by viewBinding(ActivityLoginBinding::inflate)

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)

    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    setUpView()
    initializeSubscription()
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
        viewModel.loginUser(driverId)
      }
    }
  }
}
