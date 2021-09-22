package com.flowos.app

import com.flowos.app.viewModels.FlowOSLoggerViewModel
import com.flowos.base.interfaces.Logger
import com.flowos.base.interfaces.Logger.DeviceLogLevel
import com.flowos.core.interfaces.AppResources
import timber.log.Timber
import javax.inject.Inject

class FlowOSLogger @Inject constructor(
  tree: Timber.Tree,
  private val flowOSLoggerViewModel: FlowOSLoggerViewModel,
  private val appResources: AppResources,
) : Logger {

  init {
    Timber.plant(tree)
  }

  override fun v(message: String, throwable: Throwable?) {
    Timber.v(throwable, message)
  }

  override fun d(message: String, throwable: Throwable?) {
    Timber.d(throwable, message)
  }

  override fun i(message: String, throwable: Throwable?) {
    Timber.i(throwable, message)
  }

  override fun w(message: String, throwable: Throwable?) {
    Timber.w(throwable, message)
    cacheDeviceLog(message, throwable, DeviceLogLevel.WARNING)
  }

  override fun e(message: String, throwable: Throwable?) {
    Timber.e(throwable, message)
    cacheDeviceLog(message, throwable, DeviceLogLevel.ERROR)
  }

  override fun http(url: String, method: String, request: String?, response: String?, statusCode: Int?) {
    Timber.d("$method: $url, $request\n$response\n$statusCode")
  }

  private fun cacheDeviceLog(message: String, throwable: Throwable?, deviceLogLevel: DeviceLogLevel) {
    val currentTimestamp = System.currentTimeMillis() / 1000
    flowOSLoggerViewModel.cacheDeviceLog(
      currentTimestamp,
      throwable?.stackTrace?.get(0)?.fileName ?: appResources.getString(R.string.app_name),
      deviceLogLevel,
      "$message.\n${throwable?.message}"
    )
  }
}
