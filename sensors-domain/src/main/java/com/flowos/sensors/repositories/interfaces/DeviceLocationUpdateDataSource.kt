package com.flowos.sensors.repositories.interfaces

import com.flowos.sensors.entities.DeviceLocationUpdateData
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface DeviceLocationUpdateDataSource {
  fun getDeviceLocationUpdates(): Single<List<DeviceLocationUpdateData>>

  fun saveDeviceLocationUpdates(vararg deviceLocationUpdates: DeviceLocationUpdateData): Completable

  fun clearDeviceLocationUpdates(): Completable
}
