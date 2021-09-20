package com.flowos.sensors.data.room

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class RoomConverters {

  @TypeConverter
  fun fromDoubleList(value: List<Double>) = Json.encodeToString(value)

  @TypeConverter
  fun toDoubleArray(value: String) = Json.decodeFromString<List<Double>>(value)

  @TypeConverter
  fun fromSensorMeasureRoomList(value: List<SensorMeasureRoom>) = Json.encodeToString(value)

  @TypeConverter
  fun toList(value: String): List<SensorMeasureRoom> = Json.decodeFromString(value)
}
