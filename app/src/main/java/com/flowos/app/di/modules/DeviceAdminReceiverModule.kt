package com.flowos.app.di.modules

import android.content.ComponentName
import android.content.Context
import com.flowos.app.FlowOSDeviceAdminReceiver
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object DeviceAdminReceiverModule {

  @Provides
  @Singleton
  fun providesComponentName(context: Context): ComponentName {
    return ComponentName(context.applicationContext, FlowOSDeviceAdminReceiver::class.java)
  }
}
