package com.flowos.core.di

import com.flowos.base.interfaces.UseCase
import com.flowos.core.qualifiers.GetBleDeviceHashed
import com.flowos.core.useCases.GetBleDeviceHashedUseCase
import dagger.Binds
import dagger.Module

@Module
abstract class GeneralBindsModule {

  @Binds
  @GetBleDeviceHashed
  abstract fun bindsGetBleDeviceHashedUseCase(useCase: GetBleDeviceHashedUseCase): UseCase<String, String>
}
