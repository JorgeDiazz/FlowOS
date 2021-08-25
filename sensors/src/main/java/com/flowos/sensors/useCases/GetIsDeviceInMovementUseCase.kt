package com.flowos.sensors.useCases

import com.flowos.base.interfaces.UseCase
import com.flowos.sensors.entities.SensorMeasure
import javax.inject.Inject
import kotlin.math.sqrt

private const val ACCELERATION_THRESHOLD = 10

class GetIsDeviceInMovementUseCase @Inject constructor() : UseCase<SensorMeasure, Boolean>() {

  override fun execute(input: SensorMeasure): Boolean {
    val x = input.values[0]
    val y = input.values[1]
    val z = input.values[2]

    val accelerationDiff = sqrt(x * x + y * y + z * z)

    return accelerationDiff > ACCELERATION_THRESHOLD
  }
}
