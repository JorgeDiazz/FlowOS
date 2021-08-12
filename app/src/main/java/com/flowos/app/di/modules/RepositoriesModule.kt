package com.flowos.app.di.modules

import android.content.Context
import com.flowos.auth.domain.repositories.AuthRepository
import com.flowos.auth.domain.services.AuthService
import com.flowos.auth.repositories.CredentialsRepository
import com.flowos.auth.repositories.CredentialsRepositoryImpl
import com.flowos.core.data.SharedPreferencesCache
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object RepositoriesModule {

  @Provides
  @Singleton
  fun providesCredentialsRepository(context: Context): CredentialsRepository {
    return CredentialsRepositoryImpl(
      SharedPreferencesCache(
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
