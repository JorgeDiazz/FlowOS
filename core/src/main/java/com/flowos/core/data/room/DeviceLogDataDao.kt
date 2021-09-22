package com.flowos.core.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface DeviceLogDataDao {

  @Transaction
  @Query("SELECT * FROM device_logs")
  fun getDeviceLogsData(): Single<List<DeviceLogDataRoom>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun saveDeviceLogsData(vararg deviceLogs: DeviceLogDataRoom): Completable

  @Query("DELETE FROM device_logs")
  fun clearDeviceLogsData(): Completable
}
