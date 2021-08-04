package com.flowos.app.di.modules

import com.flowos.core.interfaces.IntentResolver
import com.flowos.app.data.ResolveIntentImpl
import dagger.Module
import dagger.Provides

@Module
class IntentsModule {

  @Provides
  fun providesResolveIntentImplementation(): IntentResolver {
    return ResolveIntentImpl()
  }
}
