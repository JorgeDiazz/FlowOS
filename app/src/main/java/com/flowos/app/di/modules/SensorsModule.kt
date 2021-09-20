package com.flowos.app.di.modules

import androidx.lifecycle.ViewModel
import com.flowos.core.di.GeneralBindsModule
import com.flowos.core.di.ViewModelKey
import com.flowos.sensors.viewModels.SensorsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [GeneralBindsModule::class])
abstract class SensorsModule {
  @Binds
  @IntoMap
  @ViewModelKey(SensorsViewModel::class)
  abstract fun bindsSensorsViewModel(sensorsViewModel: SensorsViewModel): ViewModel
}
