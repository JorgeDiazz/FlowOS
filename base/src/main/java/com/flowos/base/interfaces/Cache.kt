package com.flowos.base.interfaces

import com.flowos.sensors.entities.SensorMeasure

interface Cache {
  fun saveString(key: String, value: String)

  fun readString(key: String): String?

  fun saveSensorMeasure(value: SensorMeasure)

  fun readSensorMeasures(): List<SensorMeasure>

  fun removeValue(key: String)

  fun containsValues(): Boolean

  fun clearAll()
}
