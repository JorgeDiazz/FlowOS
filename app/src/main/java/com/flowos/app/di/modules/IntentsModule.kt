package com.flowos.app.di.modules

import com.flowos.app.data.ResolveIntentImpl
import com.flowos.core.interfaces.IntentResolver
import dagger.Module
import dagger.Provides

@Module
class IntentsModule {

  @Provides
  fun providesResolveIntentImplementation(): IntentResolver {
    return ResolveIntentImpl()
  }
}
