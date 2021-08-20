package com.flowos.sensors.data

data class SensorData(
  val sensor: String,
  val timestamp: String,
  val values: List<Double>,
)
