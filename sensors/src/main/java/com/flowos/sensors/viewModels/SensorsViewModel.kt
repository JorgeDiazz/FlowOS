package com.flowos.sensors.viewModels

import android.location.Location
import android.nfc.Tag
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.flowos.base.interfaces.Cache
import com.flowos.base.interfaces.CompletableUseCase
import com.flowos.base.interfaces.Logger
import com.flowos.base.interfaces.SingleUseCase
import com.flowos.base.interfaces.UseCase
import com.flowos.base.others.BOARD_ID_KEY
import com.flowos.base.others.NFC_PAYLOAD_KEY
import com.flowos.core.BaseViewModel
import com.flowos.core.Event
import com.flowos.core.exceptions.NoConnectionException
import com.flowos.core.qualifiers.GetIsDeviceInMovement
import com.flowos.core.qualifiers.GetNfcPayloadFromNfcMeasure
import com.flowos.core.qualifiers.LongDateToTimestamp
import com.flowos.core.qualifiers.VerifyInternet
import com.flowos.sensors.data.NfcMeasure
import com.flowos.sensors.data.SensorsNews
import com.flowos.sensors.data.SensorsUiModel
import com.flowos.sensors.entities.DeviceLocationUpdateData
import com.flowos.sensors.entities.SensorMeasure
import com.flowos.sensors.qualifiers.CacheDeviceLocationUpdateDataTemporary
import com.flowos.sensors.qualifiers.PublishCachedLocationUpdates
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

class SensorsViewModel @Inject constructor(
  private val logger: Logger,
  private val cache: Cache,
  @LongDateToTimestamp private val longDateToTimestampUseCase: UseCase<Long, String>,
  @GetIsDeviceInMovement private val getIsDeviceInMovementUseCase: UseCase<SensorMeasure, Boolean>,
  @GetNfcPayloadFromNfcMeasure private val getPayloadFromNfcMeasureUseCase: UseCase<NfcMeasure, String>,
  @VerifyInternet private val verifyInternetConnectivityUseCase: SingleUseCase<Unit, Boolean>,
  @CacheDeviceLocationUpdateDataTemporary private val cacheDeviceLocationUpdateDataTemporaryUseCase: CompletableUseCase<DeviceLocationUpdateData>,
  @PublishCachedLocationUpdates private val publishCachedLocationUpdatesUseCase: CompletableUseCase<Unit>
) : BaseViewModel() {

  private val _liveData = MutableLiveData<SensorsUiModel>()
  val liveData = _liveData as LiveData<SensorsUiModel>

  private val _news = MutableLiveData<Event<SensorsNews>>()
  val news: LiveData<Event<SensorsNews>> = _news

  fun onViewActive() {
    _liveData.value = SensorsUiModel(vehicleId = getNfcPayloadFromCache())
  }

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

    validateConnectivity()
      .doOnComplete {
        attemptToPublishCachedLocationUpdates()
      }
      .doOnError {
        cacheDeviceLocationUpdateDataTemporary(deviceLocationUpdateData)
      }
      .andThen {
        // TODO: publish location updates through Google Pub/Sub topic
      }
      .subscribeOn(Schedulers.io())
      .observeOn(AndroidSchedulers.mainThread())
      .subscribe({
        _news.value = Event(SensorsNews.LocationUpdatePublished)
      }) {
        handleError(it)
      }
  }

  private fun attemptToPublishCachedLocationUpdates() {
    disposables.add(
      publishCachedLocationUpdatesUseCase.execute(Unit)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          logger.d("Cached Location Updates published and cleared successfully!")
        }) {
          handleError(it)
        }
    )
  }

  private fun cacheDeviceLocationUpdateDataTemporary(deviceLocationUpdateData: DeviceLocationUpdateData) {
    disposables.add(
      cacheDeviceLocationUpdateDataTemporaryUseCase.execute(deviceLocationUpdateData)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({
          logger.d("DeviceLocationUpdateData cached successfully!")
        }) {
          handleError(it)
        }
    )
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

  fun getNfcPayload(tag: Tag?, messages: Array<Parcelable>?) {
    val payload = getPayloadFromNfcMeasureUseCase.execute(
      NfcMeasure(tag = tag, messages = messages)
    )

    _liveData.value = SensorsUiModel(nfcPayload = payload)
  }

  private fun getNfcPayloadFromCache(): String? =
    cache.readString(NFC_PAYLOAD_KEY)

  fun cacheNfcPayload(nfcPayload: String) {
    cache.saveString(NFC_PAYLOAD_KEY, nfcPayload)
  }

  private fun validateConnectivity(): Completable {
    return Completable.defer {
      verifyInternetConnectivityUseCase.execute(Unit)
        .flatMapCompletable { isConnected ->
          if (isConnected) {
            Completable.complete()
          } else {
            Completable.error(NoConnectionException())
          }
        }
    }
  }

  private fun handleError(throwable: Throwable) {
    _news.value = Event(SensorsNews.ShowErrorNews(throwable.message.toString()))
    logger.e("SensorsViewModel handle error", throwable)
  }
}
