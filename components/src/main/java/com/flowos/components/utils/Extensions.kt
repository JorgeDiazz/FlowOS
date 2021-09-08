package com.flowos.components.utils

import android.app.Activity
import android.app.KeyguardManager
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import android.os.Parcelable
import android.view.View
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.appcompat.view.ContextThemeWrapper
import androidx.fragment.app.Fragment
import com.flowos.components.R
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import java.io.Serializable

inline fun <reified T : Serializable?> Fragment.getSerializableArgument(key: String, default: T? = null): T {
  val isNullable = null is T
  var value = arguments?.getSerializable(key)
  if (value == null || value !is T) {
    value = default
  }
  if (value == null && !isNullable) {
    throw Exception("Unable to get serializable $key from bundle argument")
  }
  return value as T
}

inline fun <reified T : Parcelable?> Fragment.getParcelableArgument(key: String, default: T? = null): T {
  val isNullable = null is T
  var value = arguments?.getParcelable<T>(key)
  if (value == null) {
    value = default
  }
  if (value == null && !isNullable) {
    throw Exception("Unable to get parcelable $key from bundle argument")
  }
  return value as T
}

fun makeErrorSnackbar(
  @NonNull view: View,
  @NonNull text: CharSequence,
  @BaseTransientBottomBar.Duration duration: Int
): Snackbar {
  return Snackbar.make(
    ContextThemeWrapper(view.context, R.style.App_Snackbar_ErrorThemeOverlay),
    view,
    text,
    duration
  )
}

fun Activity.turnScreenOn() {
  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
    setTurnScreenOn(true)
    setShowWhenLocked(true)

    (getSystemService(KeyguardManager::class.java) as KeyguardManager).requestDismissKeyguard(
      this,
      object : KeyguardManager.KeyguardDismissCallback() {
        override fun onDismissCancelled() {
          // no-op by default
        }

        override fun onDismissError() {
          // no-op by default
        }

        override fun onDismissSucceeded() {
          // no-op by default
        }
      }
    )
  } else {
    this.window.addFlags(
      WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
    )
  }

  window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}

fun Activity.isConnectedToPower(): Boolean {
  val intent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
  val plugged = intent?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
  return plugged == BatteryManager.BATTERY_PLUGGED_AC || plugged == BatteryManager.BATTERY_PLUGGED_USB || plugged == BatteryManager.BATTERY_PLUGGED_WIRELESS
}
