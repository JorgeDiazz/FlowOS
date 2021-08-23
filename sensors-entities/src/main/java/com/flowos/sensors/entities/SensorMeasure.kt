package com.flowos.sensors.entities

data class SensorMeasure(
  val sensor: String,
  val longTimestamp: Long,
  var timestamp: String? = null,
  val values: FloatArray,
) {
  enum class Sensor(val abbreviation: String) {
    ACCELEROMETER("A"), LINEAR_ACCELEROMETER("LA"), GYROSCOPE("G")
  }
}
