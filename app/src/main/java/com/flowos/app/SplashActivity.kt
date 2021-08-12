package com.flowos.app

import android.annotation.SuppressLint
import android.app.admin.DevicePolicyManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.flowos.app.data.SplashNews
import com.flowos.app.databinding.ActivitySplashBinding
import com.flowos.app.viewModels.SplashViewModel
import com.flowos.auth.LoginActivity
import com.flowos.base.interfaces.Logger
import com.flowos.base.others.ONE_SECOND_IN_MILLISECONDS
import com.flowos.components.utils.viewBinding
import com.flowos.core.EventObserver
import com.google.android.material.snackbar.Snackbar
import dagger.android.AndroidInjection
import javax.inject.Inject

/**
 * Represents splash activity.
 *
 * This is the first screen the user will watch.
 */
class SplashActivity : AppCompatActivity() {

  @Inject
  lateinit var logger: Logger

  @Inject
  lateinit var flowOSComponentName: ComponentName

  @Inject
  lateinit var viewModelFactory: ViewModelProvider.Factory

  private val viewModel: SplashViewModel by viewModels { viewModelFactory }

  private val binding by viewBinding(ActivitySplashBinding::inflate)

  private val lockTaskModeLauncher =
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
      when (result.resultCode) {
        RESULT_OK -> setLockTaskMode()
      }
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)

    super.onCreate(savedInstanceState)

    setContentView(binding.root)

    showVersionName()

    initializeLockTaskMode()
    initializeSubscription()
  }

  private fun initializeLockTaskMode() {
    val intent = Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN).apply {
      putExtra(
        DevicePolicyManager.EXTRA_DEVICE_ADMIN,
        flowOSComponentName
      )
    }

    lockTaskModeLauncher.launch(intent)
  }

  private fun setLockTaskMode() {
    (applicationContext.getSystemService(Context.DEVICE_POLICY_SERVICE) as DevicePolicyManager).apply {
      if (isDeviceOwnerApp(packageName)) {
        setLockTaskPackages(flowOSComponentName, arrayOf(packageName))
        startLockTask()
        initializeApp()

        logger.d("LockTask mode has successfully started!")
      } else {
        Snackbar.make(
          binding.root,
          getString(R.string.device_not_owner_app_message),
          Snackbar.LENGTH_INDEFINITE
        ).show()

        logger.e("This device is not owner app.")
      }
    }
  }

  private fun initializeSubscription() {
    viewModel.news.observe(this, EventObserver { handleNews(it) })
  }

  private fun initializeApp() {
    Handler(Looper.getMainLooper()).postDelayed(
      {
        viewModel.onViewActive()
      },
      ONE_SECOND_IN_MILLISECONDS
    )
  }

  @SuppressLint("SetTextI18n")
  private fun showVersionName() {
    binding.textViewVersion.text = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
  }

  private fun handleAppInitialized() {
    val mainIntent = Intent(this, MainActivity::class.java)
    startActivity(mainIntent)
    finish()
  }

  private fun handleNews(news: SplashNews) {
    when (news) {
      is SplashNews.AppInitialized -> handleAppInitialized()
      is SplashNews.ShowErrorNews -> Snackbar.make(
        binding.root,
        news.errorMessage,
        Snackbar.LENGTH_INDEFINITE
      ).show()
      is SplashNews.ShowNoConnectivityView -> showNoConnectionAlert()
      is SplashNews.OpenLoginNews -> startLoginActivity()
      is SplashNews.FinishSplashNews -> finish()
    }
  }

  private fun startLoginActivity() {
    val countrySelectionIntent = Intent(this, LoginActivity::class.java)
    startActivity(countrySelectionIntent)
    finish()
  }

  private fun showNoConnectionAlert() {
    val title = resources.getString(R.string.no_internet_title)
    val message = resources.getString(R.string.no_internet_description)
    val positiveButtonText = resources.getString(R.string.no_internet_retry_button)

    val dialog: AlertDialog.Builder = AlertDialog.Builder(this, R.style.CustomAlertDialog)
    dialog.apply {
      setTitle(title)
      setMessage(message)
      setCancelable(false)
      setPositiveButton(positiveButtonText) { dialog, _ ->
        viewModel.onViewActive()
        dialog.dismiss()
      }
      show()
    }
  }
}
