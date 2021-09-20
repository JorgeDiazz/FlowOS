package com.flowos.sensors.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface DeviceLocationUpdateDataDao {

  @Transaction
  @Query("SELECT * FROM device_location_updates")
  fun getDeviceLocationUpdates(): Single<List<DeviceLocationUpdateDataRoom>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun saveDeviceLocationUpdates(vararg deviceLocationUpdates: DeviceLocationUpdateDataRoom): Completable

  @Query("DELETE FROM device_location_updates")
  fun clearDeviceLocationUpdates(): Completable
}
