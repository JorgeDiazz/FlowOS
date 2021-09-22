package com.flowos.app.di.modules

import androidx.lifecycle.ViewModel
import com.flowos.app.viewModels.FlowOSLoggerViewModel
import com.flowos.core.di.DeviceModule
import com.flowos.core.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [DeviceModule::class])
abstract class FlowOSLoggerModule {
  @Binds
  @IntoMap
  @ViewModelKey(FlowOSLoggerViewModel::class)
  abstract fun bindsFlowOSLoggerViewModel(flowOSLoggerViewModel: FlowOSLoggerViewModel): ViewModel
}
