package com.flowos.app.di.modules

import androidx.lifecycle.ViewModel
import com.flowos.app.viewModels.SplashViewModel
import com.flowos.core.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [AppUseCasesModule::class])
abstract class SplashModule {
  @Binds
  @IntoMap
  @ViewModelKey(SplashViewModel::class)
  abstract fun bindsSplashViewModel(splashViewModel: SplashViewModel): ViewModel
}
