package com.flowos.app.di.modules

import android.content.Intent
import com.flowos.app.data.ResolveIntentImpl
import com.flowos.core.di.OpenMain
import com.flowos.core.interfaces.IntentResolver
import dagger.Module
import dagger.Provides

@Module
class IntentsModule {

  @Provides
  fun providesResolveIntentImplementation(): IntentResolver {
    return ResolveIntentImpl()
  }

  @Provides
  @OpenMain
  fun providesOpenMainAction(intentResolver: IntentResolver): () -> Intent {
    return {
      intentResolver.resolveIntent("main")
    }
  }
}
