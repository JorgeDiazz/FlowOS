package com.flowos.core.di

import com.flowos.core.data.SharedPreferencesHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class GeneralProvidesModule {
  @Provides
  @Singleton
  fun providesSharedPreferencesHelper(): SharedPreferencesHelper {
    return SharedPreferencesHelper()
  }
}
