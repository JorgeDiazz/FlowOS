package com.flowos.auth.di.modules

import com.flowos.auth.domain.data.DriverData
import com.flowos.auth.domain.data.LoginData
import com.flowos.auth.domain.useCases.LoginUserUseCase
import com.flowos.auth.useCases.IsUserLoggedUseCase
import com.flowos.base.interfaces.SingleUseCase
import com.flowos.core.di.GeneralBindsModule
import com.flowos.core.qualifiers.IsUserLogged
import com.flowos.core.qualifiers.LoginUser
import dagger.Binds
import dagger.Module

@Module(includes = [GeneralBindsModule::class])
abstract class AuthModule {

  @Binds
  @LoginUser
  abstract fun bindsLoginUserUseCase(loginUserUseCase: LoginUserUseCase): SingleUseCase<Pair<LoginData, String?>, DriverData>

  @Binds
  @IsUserLogged
  abstract fun bindsIsUserLoggedUseCase(isUserLoggedUseCase: IsUserLoggedUseCase): SingleUseCase<Unit, Boolean>
}
