package com.flowos.sensors.data

import com.flowos.sensors.entities.SensorMeasure

data class DeviceLocationUpdateData(
  val timestamp: String,
  val boardId: String,
  val coordinate: List<Double>,
  val accuracy: Float,
  val bearing: Float,
  val speed: Float,
  val sensors: List<SensorMeasure>,
)
