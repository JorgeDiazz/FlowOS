package com.flowos.app.di.modules

import androidx.lifecycle.ViewModel
import com.flowos.app.viewModels.MainViewModel
import com.flowos.core.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class MainBindsModule {
  @Binds
  @IntoMap
  @ViewModelKey(MainViewModel::class)
  abstract fun bindsMainViewModel(mainViewModel: MainViewModel): ViewModel
}
