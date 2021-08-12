package com.flowos.app.di.modules

import com.flowos.auth.di.modules.AuthModule
import dagger.Module

@Module(includes = [AppUseCasesModule::class, AuthModule::class])
abstract class FlowOSUseCasesModule
