package com.flowos.sensors.data.room

import com.flowos.sensors.entities.DeviceLocationUpdateData
import com.flowos.sensors.entities.SensorMeasure
import com.flowos.sensors.repositories.interfaces.DeviceLocationUpdateDataSource
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class DeviceLocationUpdateDataLocalSource constructor(
  private val deviceLocationUpdateDataDao: DeviceLocationUpdateDataDao
) : DeviceLocationUpdateDataSource {

  override fun getDeviceLocationUpdates(): Single<List<DeviceLocationUpdateData>> =
    deviceLocationUpdateDataDao.getDeviceLocationUpdates().map { deviceLocationUpdatesRoom ->
      deviceLocationUpdatesRoom.map {
        it.toBaseModel()
      }
    }

  override fun saveDeviceLocationUpdates(vararg deviceLocationUpdates: DeviceLocationUpdateData): Completable {
    val deviceLocationUpdatesRoom = deviceLocationUpdates.map { it.toRoomModel() }.toTypedArray()
    return deviceLocationUpdateDataDao.saveDeviceLocationUpdates(*deviceLocationUpdatesRoom)
  }

  override fun clearDeviceLocationUpdates(): Completable =
    deviceLocationUpdateDataDao.clearDeviceLocationUpdates()
}

private fun DeviceLocationUpdateDataRoom.toBaseModel(): DeviceLocationUpdateData =
  DeviceLocationUpdateData(
    timestamp = timestamp,
    boardId = boardId,
    coordinate = coordinate,
    accuracy = accuracy,
    bearing = bearing,
    speed = speed,
    sensors = sensors.map { it.toBaseModel() },
  )

private fun SensorMeasureRoom.toBaseModel(): SensorMeasure =
  SensorMeasure(
    sensor = sensor,
    longTimestamp = longTimestamp,
    timestamp = timestamp,
    values = measures,
  )

private fun DeviceLocationUpdateData.toRoomModel(): DeviceLocationUpdateDataRoom =
  DeviceLocationUpdateDataRoom(
    timestamp = timestamp,
    boardId = boardId,
    coordinate = coordinate,
    accuracy = accuracy,
    bearing = bearing,
    speed = speed,
    sensors = sensors.map { it.toRoomModel() },
  )

private fun SensorMeasure.toRoomModel(): SensorMeasureRoom =
  SensorMeasureRoom(
    sensor = sensor,
    longTimestamp = longTimestamp,
    timestamp = timestamp,
    measures = values,
  )
