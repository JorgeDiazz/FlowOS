package com.flowos.core.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "device_logs")
data class DeviceLogDataRoom(
  @PrimaryKey val timestamp: String,
  @ColumnInfo(name = "device_id") val deviceId: String,
  @ColumnInfo(name = "tag") val tag: String,
  @ColumnInfo(name = "level") val level: String,
  @ColumnInfo(name = "message") val message: String,
)
