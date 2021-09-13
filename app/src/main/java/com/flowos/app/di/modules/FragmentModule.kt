package com.flowos.app.di.modules

import com.flowos.sensors.HomeFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

  @ContributesAndroidInjector(modules = [SensorsModule::class])
  abstract fun bindHomeFragment(): HomeFragment
}
