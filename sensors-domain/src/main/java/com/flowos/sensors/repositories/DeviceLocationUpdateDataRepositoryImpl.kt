package com.flowos.sensors.repositories

import com.flowos.sensors.entities.DeviceLocationUpdateData
import com.flowos.sensors.repositories.interfaces.DeviceLocationUpdateDataRepository
import com.flowos.sensors.repositories.interfaces.DeviceLocationUpdateDataSource
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class DeviceLocationUpdateDataRepositoryImpl @Inject constructor(
  private val deviceLocationUpdateDataLocalSource: DeviceLocationUpdateDataSource
) : DeviceLocationUpdateDataRepository {

  override fun getDeviceLocationUpdates(): Single<List<DeviceLocationUpdateData>> =
    deviceLocationUpdateDataLocalSource.getDeviceLocationUpdates()

  override fun saveDeviceLocationUpdates(vararg deviceLocationUpdates: DeviceLocationUpdateData): Completable =
    deviceLocationUpdateDataLocalSource.saveDeviceLocationUpdates(*deviceLocationUpdates)

  override fun clearDeviceLocationUpdates(): Completable =
    deviceLocationUpdateDataLocalSource.clearDeviceLocationUpdates()
}
