package com.flowos.app.di.modules

import com.flowos.app.network.NetworkConnectivityInterceptor
import com.flowos.core.network.ServerInterceptor
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoSet
import okhttp3.Interceptor

@Module
abstract class InterceptorsModule {

  @Binds
  @IntoSet
  abstract fun bindsServerInterceptor(serverInterceptor: ServerInterceptor): Interceptor

  @Binds
  @IntoSet
  abstract fun bindsNetworkConnectivityInterceptor(networkConnectivityInterceptor: NetworkConnectivityInterceptor): Interceptor
}
