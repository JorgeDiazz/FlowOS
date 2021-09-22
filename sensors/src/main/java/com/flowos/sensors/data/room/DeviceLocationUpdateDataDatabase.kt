package com.flowos.sensors.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.flowos.core.data.room.DeviceLogDataDao
import com.flowos.core.data.room.DeviceLogDataRoom

@Database(entities = [DeviceLocationUpdateDataRoom::class, DeviceLogDataRoom::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class DeviceLocationUpdateDataDatabase : RoomDatabase() {
  abstract fun deviceLocationUpdateDataDao(): DeviceLocationUpdateDataDao

  abstract fun deviceLogDataDao(): DeviceLogDataDao
}
