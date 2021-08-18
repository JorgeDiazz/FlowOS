package com.flowos.app.di.modules

import com.flowos.sensors.SensorsFragment
import com.flowos.sensors.di.SensorsModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

  @ContributesAndroidInjector(modules = [SensorsModule::class])
  abstract fun bindSensorsFragment(): SensorsFragment
}
