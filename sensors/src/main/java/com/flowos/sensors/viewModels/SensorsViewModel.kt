package com.flowos.sensors.viewModels

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flowos.base.interfaces.Cache
import com.flowos.base.interfaces.Logger
import com.flowos.base.interfaces.UseCase
import com.flowos.base.others.BOARD_ID_KEY
import com.flowos.core.BaseViewModel
import com.flowos.core.Event
import com.flowos.core.interfaces.AppResources
import com.flowos.core.qualifiers.GetIsDeviceInMovement
import com.flowos.core.qualifiers.LongDateToTimestamp
import com.flowos.sensors.data.DeviceLocationUpdateData
import com.flowos.sensors.data.SensorsNews
import com.flowos.sensors.data.SensorsUiModel
import com.flowos.sensors.entities.SensorMeasure
import javax.inject.Inject

class SensorsViewModel @Inject constructor(
  private val logger: Logger,
  private val appResources: AppResources,
  private val cache: Cache,
  @LongDateToTimestamp private val longDateToTimestampUseCase: UseCase<Long, String>,
  @GetIsDeviceInMovement private val getIsDeviceInMovementUseCase: UseCase<SensorMeasure, Boolean>,
) : BaseViewModel() {

  private val _liveData = MutableLiveData<SensorsUiModel>()
  val liveData = _liveData as LiveData<SensorsUiModel>

  private val _news = MutableLiveData<Event<SensorsNews>>()
  val news: LiveData<Event<SensorsNews>> = _news

  fun sendDeviceLocationUpdate(location: Location) {
    val deviceLocationUpdateData = DeviceLocationUpdateData(
      timestamp = longDateToTimestampUseCase.execute(location.time),
      boardId = cache.readString(BOARD_ID_KEY).orEmpty(),
      coordinate = listOf(location.latitude, location.longitude),
      accuracy = location.accuracy,
      bearing = location.bearing,
      speed = location.speed,
      sensors = cache.readSensorMeasures()
    )

    logger.d("deviceLocationUpdateData $deviceLocationUpdateData")

    // TODO: publish location updates through Google Pub/Sub topic
    _news.value = Event(SensorsNews.LocationUpdatePublished)
  }

  fun cacheSensorMeasure(sensorMeasure: SensorMeasure) {
    sensorMeasure.let {
      it.timestamp = longDateToTimestampUseCase.execute(it.longTimestamp)
      cache.saveSensorMeasure(it)
    }
  }

  fun detectMovement(accelerometerSensorMeasure: SensorMeasure) {
    val isDeviceInMovement = getIsDeviceInMovementUseCase.execute(accelerometerSensorMeasure)

    if (!isDeviceInMovement) {
      _news.value = Event(SensorsNews.NoDeviceMovement)
    }
  }
}
