package com.flowos.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.flowos.app.databinding.ActivityMainBinding
import com.flowos.auth.LoginActivity
import com.flowos.base.interfaces.Logger
import com.flowos.base.others.LOG_OFF_INTENT_FILTER
import com.flowos.base.others.TURN_SCREEN_INTENT_FILTER
import com.flowos.base.others.TURN_SCREEN_ON
import com.flowos.components.utils.turnScreenOn
import com.flowos.components.utils.viewBinding
import com.flowos.sensors.HomeFragment
import dagger.android.AndroidInjection
import javax.inject.Inject

/**
 * Represents main activity.
 *
 * This is the orchestrator of app's views.
 */
class MainActivity : AppCompatActivity() {

  @Inject
  lateinit var logger: Logger

  private val binding by viewBinding(ActivityMainBinding::inflate)

  private val turnScreenReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      if (intent.getBooleanExtra(TURN_SCREEN_ON, false)) {
        turnScreenOn()
      }
    }
  }

  private val logOffReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
      startLoginActivity()
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    AndroidInjection.inject(this)

    super.onCreate(savedInstanceState)
    setContentView(binding.root)

    setUpTurnScreenBroadcast()
    setUpLogOffBroadcast()

    loadFragment(HomeFragment.newInstance())
  }

  private fun loadFragment(
    fragment: Fragment,
    addToBackStackName: String? = null
  ) {
    with(supportFragmentManager.beginTransaction()) {
      replace(R.id.layout_container, fragment)
      addToBackStackName?.let {
        addToBackStack(addToBackStackName)
      }
      commit()
    }
  }

  private fun setUpTurnScreenBroadcast() {
    LocalBroadcastManager.getInstance(applicationContext)
      .registerReceiver(
        turnScreenReceiver,
        IntentFilter(TURN_SCREEN_INTENT_FILTER)
      )
  }

  private fun setUpLogOffBroadcast() {
    LocalBroadcastManager.getInstance(applicationContext)
      .registerReceiver(
        logOffReceiver,
        IntentFilter(LOG_OFF_INTENT_FILTER)
      )
  }

  private fun startLoginActivity() {
    val countrySelectionIntent = Intent(this, LoginActivity::class.java)
    startActivity(countrySelectionIntent)
    finish()
  }

  override fun onBackPressed() {
    // no-op by default
  }
}
