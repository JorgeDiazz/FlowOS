package com.flowos.app.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.flowos.app.SplashActivity

class AppStartupReceiver : BroadcastReceiver() {

  override fun onReceive(context: Context, intent: Intent) {
    if (intent.action.equals(Intent.ACTION_BOOT_COMPLETED)) {
      val splashIntent = Intent(context, SplashActivity::class.java)
      splashIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      context.startActivity(splashIntent)
    }
  }
}
