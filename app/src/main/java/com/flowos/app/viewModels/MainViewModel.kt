package com.flowos.app.viewModels

import androidx.lifecycle.LifecycleObserver
import com.flowos.base.interfaces.Logger
import com.flowos.core.BaseViewModel
import com.flowos.core.interfaces.AppResources
import javax.inject.Inject

class MainViewModel @Inject constructor(
  private val logger: Logger,
  private val appResources: AppResources,
) : BaseViewModel(), LifecycleObserver
