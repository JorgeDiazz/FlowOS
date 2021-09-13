package com.flowos.app.di.modules

import android.app.Service
import com.flowos.app.workers.SensorsService
import dagger.Binds
import dagger.Module

@Module
abstract class SensorsServiceModule {
  @Binds
  abstract fun bindsSensorsService(sensorsService: SensorsService): Service
}
