package com.flowos.app.di.modules

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.flowos.app.data.FlowOSResources
import com.flowos.app.di.ViewModelFactory
import com.flowos.core.interfaces.AppResources
import dagger.Binds
import dagger.Module

@Module
abstract class BaseModule {
  @Binds
  abstract fun bindViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory

  @Binds
  abstract fun bindContext(flowosApp: Application): Context

  @Binds
  abstract fun bindResources(flowosResources: FlowOSResources): AppResources
}
