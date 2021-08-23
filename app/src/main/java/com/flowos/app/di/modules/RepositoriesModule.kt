package com.flowos.app.di.modules

import android.content.Context
import com.flowos.auth.domain.repositories.AuthRepository
import com.flowos.auth.domain.services.AuthService
import com.flowos.auth.repositories.CredentialsRepository
import com.flowos.auth.repositories.CredentialsRepositoryImpl
import com.flowos.base.interfaces.Cache
import com.flowos.core.data.CacheImpl
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object RepositoriesModule {

  @Provides
  @Singleton
  fun providesCache(context: Context): Cache {
    return CacheImpl(
      name = "general",
      context = context.applicationContext
    )
  }

  @Provides
  @Singleton
  fun providesCredentialsRepository(context: Context): CredentialsRepository {
    return CredentialsRepositoryImpl(
      CacheImpl(
        name = "credentials",
        context = context.applicationContext
      )
    )
  }

  @Provides
  @Singleton
  fun bindsAuthRepository(authService: AuthService): AuthRepository {
    return AuthRepository(authService)
  }
}
