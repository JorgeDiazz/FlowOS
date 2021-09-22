package com.flowos.core.useCases

import android.util.Log
import com.flowos.base.interfaces.CompletableUseCase
import com.flowos.core.data.DeviceLogData
import com.flowos.core.interfaces.DeviceLogDataRepository
import io.reactivex.rxjava3.core.Completable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val CACHE_TIME_TO_LIVE_IN_MINUTES = 5L

class CacheDeviceLogDataTemporaryUseCase @Inject constructor(
  private val deviceLogDataRepository: DeviceLogDataRepository,
) : CompletableUseCase<DeviceLogData>() {

  override fun execute(input: DeviceLogData): Completable {
    return deviceLogDataRepository.saveDeviceLogsData(input)
      .doOnComplete { Log.d("DeviceLogsData", "Cached successfully") }
      .doOnError { Log.e("cacheDeviceLog", it.message.orEmpty()) }
      .delay(CACHE_TIME_TO_LIVE_IN_MINUTES, TimeUnit.MINUTES)
      .andThen(
        flushDeviceLogsData()
      )
      .andThen(
        clearDeviceLogsData()
      )
  }

  private fun flushDeviceLogsData(): Completable {
    return deviceLogDataRepository.getDeviceLogsData()
      .concatMapCompletable {
        // TODO: publish device logs data through Google Pub/Sub topic
        Completable.complete()
      }
      .doOnComplete { Log.d("flushDeviceLogsData", "Flushed DeviceLogsData database successfully!") }
      .doOnError { Log.e("flushDeviceLogsData", it.message.orEmpty()) }
  }

  private fun clearDeviceLogsData(): Completable {
    return deviceLogDataRepository.clearDeviceLogsData()
      .doOnComplete { Log.d("clearDeviceLogsData", "Cleared DeviceLogsData database successfully!") }
      .doOnError { Log.e("clearDeviceLogsData", it.message.orEmpty()) }
  }
}
