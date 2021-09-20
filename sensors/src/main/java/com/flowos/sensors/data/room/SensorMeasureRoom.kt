package com.flowos.sensors.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.serialization.Serializable

@Entity
@Serializable
data class SensorMeasureRoom(
  @ColumnInfo(name = "sensor") val sensor: String,
  @ColumnInfo(name = "long_timestamp") val longTimestamp: Long,
  @ColumnInfo(name = "timestamp") val timestamp: String?,
  @ColumnInfo(name = "measures") val measures: FloatArray,
)
