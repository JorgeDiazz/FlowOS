package com.flowos.app.di.modules

import com.flowos.app.workers.SensorsService
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ServiceModule {

  @ContributesAndroidInjector(modules = [SensorsServiceModule::class])
  abstract fun bindSensorsService(): SensorsService
}
