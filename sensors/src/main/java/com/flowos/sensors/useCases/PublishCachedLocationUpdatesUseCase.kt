package com.flowos.sensors.useCases

import com.flowos.base.interfaces.CompletableUseCase
import com.flowos.sensors.repositories.interfaces.DeviceLocationUpdateDataRepository
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

class PublishCachedLocationUpdatesUseCase @Inject constructor(
  private val deviceLocationUpdateDataRepository: DeviceLocationUpdateDataRepository
) : CompletableUseCase<Unit>() {

  override fun execute(input: Unit): Completable {
    return deviceLocationUpdateDataRepository.getDeviceLocationUpdates().map {
      it // TODO: publish location updates through Google Pub/Sub topic
    }.concatMapCompletable {
      deviceLocationUpdateDataRepository.clearDeviceLocationUpdates()
    }
  }
}
