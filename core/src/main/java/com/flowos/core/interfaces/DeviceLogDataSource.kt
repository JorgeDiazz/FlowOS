package com.flowos.core.interfaces

import com.flowos.core.data.DeviceLogData
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface DeviceLogDataSource {
  fun getDeviceLogsData(): Single<List<DeviceLogData>>

  fun saveDeviceLogsData(vararg deviceLogs: DeviceLogData): Completable

  fun clearDeviceLogsData(): Completable
}
