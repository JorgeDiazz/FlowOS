package com.flowos.sensors.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_location_updates")
data class DeviceLocationUpdateDataRoom(
  @PrimaryKey val timestamp: String,
  @ColumnInfo(name = "board_id") val boardId: String,
  @ColumnInfo(name = "coordinate") val coordinate: List<Double>,
  @ColumnInfo(name = "accuracy") val accuracy: Float,
  @ColumnInfo(name = "bearing") val bearing: Float,
  @ColumnInfo(name = "speed") val speed: Float,
  @ColumnInfo(name = "sensors") val sensors: List<SensorMeasureRoom>,
)
