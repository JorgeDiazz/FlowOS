package com.flowos.app.di.modules

import android.content.ContentResolver
import android.content.Context
import com.flowos.base.interfaces.Logger
import com.flowos.core.interfaces.AppResources
import com.flowos.app.viewModels.FlowOSAppViewModel
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object FlowOSAppModule {
  @Provides
  fun postFlowOSAppViewModel(
    logger: Logger,
    appResources: AppResources,
  ): FlowOSAppViewModel {
    return FlowOSAppViewModel(
      logger,
      appResources,
    )
  }

  @Provides
  @Singleton
  fun providesContentResolver(context: Context): ContentResolver {
    return context.contentResolver
  }
}
