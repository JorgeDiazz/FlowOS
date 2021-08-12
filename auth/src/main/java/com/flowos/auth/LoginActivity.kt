package com.flowos.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.flowos.auth.data.LoginNews
import com.flowos.auth.databinding.ActivityLoginBinding
import com.flowos.auth.viewModels.LoginViewModel
import com.flowos.base.interfaces.Logger
import com.flowos.components.utils.viewBinding
import com.flowos.core.EventObserver
import com.flowos.core.di.OpenMain
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
  @OpenMain
  @JvmSuppressWildcards
  lateinit var openMainAction: () -> Intent

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
      is LoginNews.LoginSuccessful -> startMainActivity()
      is LoginNews.ShowErrorNews -> Snackbar.make(
        binding.root,
        news.message,
        Snackbar.LENGTH_LONG
      ).show()
    }
  }

  private fun startMainActivity() {
    startActivity(openMainAction())
    finish()
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
