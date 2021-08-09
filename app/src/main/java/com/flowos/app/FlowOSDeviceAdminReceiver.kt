package com.flowos.app

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.flowos.base.interfaces.Logger
import javax.inject.Inject

class FlowOSDeviceAdminReceiver : DeviceAdminReceiver() {

  @Inject
  lateinit var logger: Logger

  override fun onEnabled(context: Context, intent: Intent) =
    showToast(context, context.getString(R.string.on_enabled_admin_receiver_message))

  override fun onDisableRequested(context: Context, intent: Intent): CharSequence =
    context.getString(R.string.on_disable_requested_message)

  override fun onDisabled(context: Context, intent: Intent) =
    logger.e(context.getString(R.string.on_disabled_admin_receiver_message))

  private fun showToast(context: Context, message: String) {
    message.let { status ->
      Toast.makeText(context, status, Toast.LENGTH_LONG).show()
    }
  }
}
