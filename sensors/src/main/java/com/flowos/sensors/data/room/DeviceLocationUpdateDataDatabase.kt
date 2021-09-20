package com.flowos.sensors.data.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [DeviceLocationUpdateDataRoom::class], version = 1, exportSchema = false)
@TypeConverters(RoomConverters::class)
abstract class DeviceLocationUpdateDataDatabase : RoomDatabase() {
  abstract fun deviceLocationUpdateDataDao(): DeviceLocationUpdateDataDao
}
