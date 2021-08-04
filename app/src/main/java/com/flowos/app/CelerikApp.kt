package com.flowos.app

import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import androidx.lifecycle.ProcessLifecycleOwner
import com.flowos.core.CoreApp
import com.flowos.app.di.components.AppComponent
import com.flowos.app.di.components.DaggerAppComponent
import com.flowos.app.viewModels.FlowOSAppViewModel
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import javax.inject.Inject

open class FlowOSApp : CoreApp(), HasAndroidInjector {

  private lateinit var appComponent: AppComponent

  @Inject
  lateinit var dispatchingActivityInjector: DispatchingAndroidInjector<Any>

  @Inject
  lateinit var viewModel: FlowOSAppViewModel

  override fun onCreate() {
    initializeStrictMode()
    super.onCreate()

    initializeComponent()
    ProcessLifecycleOwner.get().lifecycle.addObserver(viewModel)
  }

  private fun initializeComponent() {
    appComponent = DaggerAppComponent.builder()
      .application(this)
      .build()

    appComponent.inject(this)
  }

  override fun androidInjector(): AndroidInjector<Any> {
    return dispatchingActivityInjector
  }

  private fun initializeStrictMode() {
    if (BuildConfig.DEBUG) {
      StrictMode.setThreadPolicy(
        ThreadPolicy.Builder()
          .detectAll()
          .permitDiskReads()
          .penaltyLog()
          .build()
      )
      StrictMode.setVmPolicy(
        VmPolicy.Builder()
          .detectLeakedSqlLiteObjects()
          .detectLeakedClosableObjects()
          .penaltyLog()
          .penaltyDeath()
          .build()
      )
    }
  }
}
