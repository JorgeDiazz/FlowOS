package com.flowos.sensors.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flowos.base.interfaces.Logger
import com.flowos.core.BaseViewModel
import com.flowos.core.Event
import com.flowos.core.interfaces.AppResources
import com.flowos.sensors.data.SensorsNews
import com.flowos.sensors.data.SensorsUiModel
import javax.inject.Inject

class SensorsViewModel @Inject constructor(
  private val logger: Logger,
  private val appResources: AppResources,
) : BaseViewModel() {

  private val _liveData = MutableLiveData<SensorsUiModel>()
  val liveData = _liveData as LiveData<SensorsUiModel>

  private val _news = MutableLiveData<Event<SensorsNews>>()
  val news: LiveData<Event<SensorsNews>> = _news

  fun onViewActive() {
  }
}
