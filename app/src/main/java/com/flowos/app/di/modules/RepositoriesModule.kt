package com.flowos.app.di.modules

import android.content.Context
import androidx.room.Room
import com.flowos.auth.domain.repositories.AuthRepository
import com.flowos.auth.domain.services.AuthService
import com.flowos.auth.repositories.CredentialsRepository
import com.flowos.auth.repositories.CredentialsRepositoryImpl
import com.flowos.base.interfaces.Cache
import com.flowos.core.DeviceLogDataRepositoryImpl
import com.flowos.core.data.CacheImpl
import com.flowos.core.data.room.DeviceLogDataLocalSource
import com.flowos.core.interfaces.DeviceLogDataRepository
import com.flowos.sensors.data.room.DeviceLocationUpdateDataDatabase
import com.flowos.sensors.data.room.DeviceLocationUpdateDataLocalSource
import com.flowos.sensors.repositories.DeviceLocationUpdateDataRepositoryImpl
import com.flowos.sensors.repositories.interfaces.DeviceLocationUpdateDataRepository
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

  @Provides
  @Singleton
  fun providesDeviceLocationUpdateDataRepository(
    context: Context,
  ): DeviceLocationUpdateDataRepository {
    val database =
      Room.databaseBuilder(context, DeviceLocationUpdateDataDatabase::class.java, "database-device_location_updates")
        .fallbackToDestructiveMigration()
        .build()

    val dao = database.deviceLocationUpdateDataDao()
    val dataSource = DeviceLocationUpdateDataLocalSource(dao)

    return DeviceLocationUpdateDataRepositoryImpl(dataSource)
  }

  @Provides
  @Singleton
  fun providesDeviceLogDataRepository(
    context: Context,
  ): DeviceLogDataRepository {
    val database =
      Room.databaseBuilder(context, DeviceLocationUpdateDataDatabase::class.java, "database-device_logs")
        .fallbackToDestructiveMigration()
        .build()

    val dao = database.deviceLogDataDao()
    val dataSource = DeviceLogDataLocalSource(dao)

    return DeviceLogDataRepositoryImpl(dataSource)
  }
}
