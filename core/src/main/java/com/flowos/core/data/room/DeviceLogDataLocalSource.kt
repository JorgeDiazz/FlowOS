package com.flowos.core.data.room

import com.flowos.core.data.DeviceLogData
import com.flowos.core.interfaces.DeviceLogDataSource
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class DeviceLogDataLocalSource constructor(
  private val deviceLogDataDao: DeviceLogDataDao
) : DeviceLogDataSource {

  override fun getDeviceLogsData(): Single<List<DeviceLogData>> =
    deviceLogDataDao.getDeviceLogsData().map { deviceLogsDataRoom ->
      deviceLogsDataRoom.map {
        it.toBaseModel()
      }
    }

  override fun saveDeviceLogsData(vararg deviceLogs: DeviceLogData): Completable {
    val deviceLogDataRoom = deviceLogs.map { it.toRoomModel() }.toTypedArray()
    return deviceLogDataDao.saveDeviceLogsData(*deviceLogDataRoom)
  }

  override fun clearDeviceLogsData(): Completable =
    deviceLogDataDao.clearDeviceLogsData()
}

private fun DeviceLogData.toRoomModel() =
  DeviceLogDataRoom(
    deviceId = deviceId,
    timestamp = timestamp,
    tag = tag,
    level = level,
    message = message,
  )

private fun DeviceLogDataRoom.toBaseModel() =
  DeviceLogData(
    deviceId = deviceId,
    timestamp = timestamp,
    tag = tag,
    level = level,
    message = message,
  )
