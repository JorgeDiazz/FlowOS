package com.flowos.app.viewModels

import androidx.lifecycle.LifecycleObserver
import com.flowos.base.interfaces.CompletableUseCase
import com.flowos.base.interfaces.Logger
import com.flowos.base.interfaces.UseCase
import com.flowos.core.BaseViewModel
import com.flowos.core.data.DeviceLogData
import com.flowos.core.qualifiers.CacheDeviceLogDataTemporary
import com.flowos.core.qualifiers.GetDeviceId
import com.flowos.core.qualifiers.LongDateToTimestamp
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.Locale
import javax.inject.Inject

private const val CACHE_TIME_TO_LIVE_IN_MINUTES = 5L

class FlowOSLoggerViewModel @Inject constructor(
  @GetDeviceId private val getDeviceIdUseCase: UseCase<Unit, String>,
  @LongDateToTimestamp private val longDateToTimestampUseCase: UseCase<Long, String>,
  @CacheDeviceLogDataTemporary private val cacheDeviceLogDataTemporaryUseCase: CompletableUseCase<DeviceLogData>,
) : BaseViewModel(), LifecycleObserver {

  fun cacheDeviceLog(timestamp: Long, tag: String, level: Logger.DeviceLogLevel, message: String) {
    val deviceLogData = DeviceLogData(
      deviceId = getDeviceIdUseCase.execute(Unit),
      timestamp = longDateToTimestampUseCase.execute(timestamp),
      tag = tag,
      level = level.name.toLowerCase(Locale.ENGLISH),
      message = message
    )

    cacheDeviceLogDataTemporaryUseCase.execute(deviceLogData)
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe()
  }
}
