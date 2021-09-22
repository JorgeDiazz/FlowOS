package com.flowos.core.di

import com.flowos.base.interfaces.CompletableUseCase
import com.flowos.base.interfaces.UseCase
import com.flowos.core.data.DeviceLogData
import com.flowos.core.qualifiers.CacheDeviceLogDataTemporary
import com.flowos.core.qualifiers.GetDeviceId
import com.flowos.core.useCases.CacheDeviceLogDataTemporaryUseCase
import com.flowos.core.useCases.GetDeviceIdUseCase
import dagger.Binds
import dagger.Module

@Module
abstract class DeviceModule {
  @Binds
  @GetDeviceId
  abstract fun bindsGetDeviceIdUseCase(getDeviceIdUseCase: GetDeviceIdUseCase): UseCase<Unit, String>

  @Binds
  @CacheDeviceLogDataTemporary
  abstract fun bindsCacheDeviceLogDataTemporaryUseCase(cacheDeviceLogDataTemporaryUseCase: CacheDeviceLogDataTemporaryUseCase): CompletableUseCase<DeviceLogData>
}
