package com.flowos.app.di.modules

import androidx.lifecycle.ViewModel
import com.flowos.auth.di.modules.AuthModule
import com.flowos.auth.viewModels.LoginViewModel
import com.flowos.core.di.DeviceModule
import com.flowos.core.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [AppUseCasesModule::class, AuthModule::class, DeviceModule::class])
abstract class LoginModule {
  @Binds
  @IntoMap
  @ViewModelKey(LoginViewModel::class)
  abstract fun bindsLoginViewModel(loginViewModel: LoginViewModel): ViewModel
}
