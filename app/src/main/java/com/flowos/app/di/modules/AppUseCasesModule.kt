package com.flowos.app.di.modules

import com.flowos.base.interfaces.SingleUseCase
import com.flowos.core.qualifiers.VerifyInternet
import com.flowos.app.useCases.VerifyInternetConnectivityUseCase
import dagger.Binds
import dagger.Module

@Module
abstract class AppUseCasesModule {

  @Binds
  @VerifyInternet
  abstract fun bindVerifyInternetConnectivityUseCase(useCase: VerifyInternetConnectivityUseCase): SingleUseCase<Unit, Boolean>
}
