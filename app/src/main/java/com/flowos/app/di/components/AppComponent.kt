package com.flowos.app.di.components

import android.app.Application
import com.flowos.app.FlowOSApp
import com.flowos.app.di.modules.ActivityModule
import com.flowos.app.di.modules.BaseModule
import com.flowos.app.di.modules.DeviceAdminReceiverModule
import com.flowos.app.di.modules.FlowOSAppModule
import com.flowos.app.di.modules.FlowOSUseCasesModule
import com.flowos.app.di.modules.FragmentModule
import com.flowos.app.di.modules.IntentsModule
import com.flowos.app.di.modules.LoggerModule
import com.flowos.app.di.modules.NetworkModule
import com.flowos.app.di.modules.NetworkServicesModule
import com.flowos.app.di.modules.RepositoriesModule
import com.flowos.app.di.modules.ServiceModule
import com.flowos.core.CoreComponent
import com.flowos.sensors.di.modules.SensorsCacheModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
  modules = [
    IntentsModule::class,
    AndroidInjectionModule::class,
    AndroidSupportInjectionModule::class,
    BaseModule::class,
    NetworkModule::class,
    NetworkServicesModule::class,
    ActivityModule::class,
    FragmentModule::class,
    ServiceModule::class,
    LoggerModule::class,
    RepositoriesModule::class,
    FlowOSAppModule::class,
    FlowOSUseCasesModule::class,
    DeviceAdminReceiverModule::class,
    SensorsCacheModule::class,
  ]
)
interface AppComponent : CoreComponent, AndroidInjector<FlowOSApp> {

  @Component.Builder
  interface Builder {
    @BindsInstance
    fun application(application: Application): Builder

    fun build(): AppComponent
  }
}
