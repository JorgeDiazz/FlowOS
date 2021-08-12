package com.flowos.app.di.modules

import com.flowos.app.MainActivity
import com.flowos.app.SplashActivity
import com.flowos.auth.LoginActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

  @ContributesAndroidInjector(modules = [SplashModule::class])
  abstract fun bindSplashActivity(): SplashActivity

  @ContributesAndroidInjector(modules = [MainBindsModule::class])
  abstract fun bindMainActivity(): MainActivity

  @ContributesAndroidInjector(modules = [LoginModule::class])
  abstract fun bindLoginActivity(): LoginActivity
}
