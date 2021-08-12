package com.flowos.app.di.modules

import com.flowos.auth.domain.services.AuthService
import com.flowos.core.di.RetrofitFlowOS
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit

@Module
object NetworkServicesModule {
  @Provides
  fun provideAuthServices(@RetrofitFlowOS retrofit: Retrofit): AuthService {
    return retrofit.create(AuthService::class.java)
  }
}
