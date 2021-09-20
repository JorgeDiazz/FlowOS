package com.flowos.sensors.di.modules

import com.flowos.base.interfaces.CompletableUseCase
import com.flowos.sensors.entities.DeviceLocationUpdateData
import com.flowos.sensors.qualifiers.CacheDeviceLocationUpdateDataTemporary
import com.flowos.sensors.qualifiers.PublishCachedLocationUpdates
import com.flowos.sensors.useCases.CacheDeviceLocationUpdateDataTemporaryUseCase
import com.flowos.sensors.useCases.PublishCachedLocationUpdatesUseCase
import dagger.Binds
import dagger.Module

@Module
abstract class SensorsCacheModule {

  @Binds
  @CacheDeviceLocationUpdateDataTemporary
  abstract fun bindsCacheDeviceLocationUpdateDataTemporaryUseCase(useCase: CacheDeviceLocationUpdateDataTemporaryUseCase): CompletableUseCase<DeviceLocationUpdateData>

  @Binds
  @PublishCachedLocationUpdates
  abstract fun bindsPublishCachedLocationUpdatesUseCase(useCase: PublishCachedLocationUpdatesUseCase): CompletableUseCase<Unit>
}
