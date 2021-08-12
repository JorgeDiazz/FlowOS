package com.flowos.core.di

import com.flowos.base.interfaces.UseCase
import com.flowos.core.qualifiers.GetDeviceId
import com.flowos.core.useCases.GetDeviceIdUseCase
import dagger.Binds
import dagger.Module

@Module
abstract class DeviceModule {
  @Binds
  @GetDeviceId
  abstract fun bindsGetDeviceIdUseCase(getDeviceIdUseCase: GetDeviceIdUseCase): UseCase<Unit, String>
}
