package com.flowos.app.di.modules

import com.flowos.app.FlowOSDebugTree
import com.flowos.app.FlowOSLogger
import com.flowos.base.interfaces.Logger
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object LoggerModule {
  @Provides
  @Singleton
  fun providesLoggerImplementation(): Logger {
    val tree = FlowOSDebugTree() // The logger could be changed according to current environment
    return FlowOSLogger(tree)
  }
}
