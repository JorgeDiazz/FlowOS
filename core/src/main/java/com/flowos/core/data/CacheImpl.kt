package com.flowos.core.data

import android.content.Context
import android.content.SharedPreferences
import androidx.collection.LruCache
import com.flowos.base.interfaces.Cache
import com.flowos.sensors.entities.SensorMeasure
import java.util.UUID

private const val MAX_MEASURES_PER_SENSOR = 20

class CacheImpl(name: String, context: Context) : Cache {

  private var sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
  private val editor: SharedPreferences.Editor = sharedPreferences.edit()

  private val accelerometerLruCache: LruCache<String, SensorMeasure> = LruCache(MAX_MEASURES_PER_SENSOR)
  private val linearAccelerometerLruCache: LruCache<String, SensorMeasure> = LruCache(MAX_MEASURES_PER_SENSOR)
  private val gyroscopeLruCache: LruCache<String, SensorMeasure> = LruCache(MAX_MEASURES_PER_SENSOR)

  override fun saveString(key: String, value: String) {
    editor.putString(key, value)
    editor.commit()
  }

  override fun readString(key: String): String? {
    return sharedPreferences.getString(key, null)
  }

  override fun saveSensorMeasure(value: SensorMeasure) {
    val randomKey = UUID.randomUUID().toString()

    when (value.sensor) {
      SensorMeasure.Sensor.ACCELEROMETER.abbreviation -> accelerometerLruCache.put(randomKey, value)
      SensorMeasure.Sensor.LINEAR_ACCELEROMETER.abbreviation -> linearAccelerometerLruCache.put(randomKey, value)
      SensorMeasure.Sensor.GYROSCOPE.abbreviation -> gyroscopeLruCache.put(randomKey, value)
    }
  }

  override fun readSensorMeasures(): List<SensorMeasure> {
    return listOf(
      accelerometerLruCache.snapshot().values.toList(),
      linearAccelerometerLruCache.snapshot().values.toList(),
      gyroscopeLruCache.snapshot().values.toList()
    ).flatten()
  }

  override fun removeValue(key: String) {
    editor.remove(key)
    editor.apply()
  }

  override fun containsValues(): Boolean {
    return sharedPreferences.all.isNotEmpty()
  }

  override fun clearAll() {
    editor.clear()
    editor.apply()
  }
}
