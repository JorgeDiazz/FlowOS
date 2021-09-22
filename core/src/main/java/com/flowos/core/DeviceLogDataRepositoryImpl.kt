package com.flowos.core

import com.flowos.core.data.DeviceLogData
import com.flowos.core.interfaces.DeviceLogDataRepository
import com.flowos.core.interfaces.DeviceLogDataSource
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class DeviceLogDataRepositoryImpl @Inject constructor(
  private val deviceLogDataLocalSource: DeviceLogDataSource
) : DeviceLogDataRepository {

  override fun getDeviceLogsData(): Single<List<DeviceLogData>> =
    deviceLogDataLocalSource.getDeviceLogsData()

  override fun saveDeviceLogsData(vararg deviceLogs: DeviceLogData): Completable =
    deviceLogDataLocalSource.saveDeviceLogsData(*deviceLogs)

  override fun clearDeviceLogsData(): Completable =
    deviceLogDataLocalSource.clearDeviceLogsData()
}
