package com.flowos.sensors.useCases

import com.flowos.base.interfaces.CompletableUseCase
import com.flowos.base.interfaces.Logger
import com.flowos.sensors.entities.DeviceLocationUpdateData
import com.flowos.sensors.repositories.interfaces.DeviceLocationUpdateDataRepository
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val CACHE_TIME_TO_LIVE_IN_MINUTES = 5L

class CacheDeviceLocationUpdateDataTemporaryUseCase @Inject constructor(
  private val deviceLocationUpdateDataRepository: DeviceLocationUpdateDataRepository,
  private val logger: Logger,
) : CompletableUseCase<DeviceLocationUpdateData>() {

  override fun execute(input: DeviceLocationUpdateData): Completable =
    deviceLocationUpdateDataRepository.saveDeviceLocationUpdates(input)
      .delay(CACHE_TIME_TO_LIVE_IN_MINUTES, TimeUnit.MINUTES).andThen {
        clearDeviceLocationUpdates()
      }

  private fun clearDeviceLocationUpdates() {
    deviceLocationUpdateDataRepository.clearDeviceLocationUpdates()
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe(
        { logger.d("Cleared DeviceLocationUpdates database successfully!") },
        { logger.e("clearDeviceLocationUpdates", it) }
      )
  }
}
