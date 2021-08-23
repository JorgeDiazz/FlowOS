package com.flowos.sensors.listeners

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import com.flowos.base.utils.multiLet
import com.flowos.sensors.entities.SensorMeasure

class MotionSensorListener(
  private val sensorCallback: (SensorMeasure) -> Unit,
) : SensorEventListener {

  override fun onSensorChanged(event: SensorEvent?) {
    val sensorType = when (event?.sensor?.type) {
      Sensor.TYPE_ACCELEROMETER -> SensorMeasure.Sensor.ACCELEROMETER.abbreviation
      Sensor.TYPE_LINEAR_ACCELERATION -> SensorMeasure.Sensor.LINEAR_ACCELEROMETER.abbreviation
      Sensor.TYPE_GYROSCOPE -> SensorMeasure.Sensor.GYROSCOPE.abbreviation
      else -> null
    }

    multiLet(event, sensorType) { (event, sensorType) ->
      sensorCallback.invoke(
        SensorMeasure(
          sensor = sensorType.toString(),
          longTimestamp = (event as SensorEvent).timestamp,
          values = event.values
        )
      )
    }
  }

  override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    // no-op by default
  }
}
