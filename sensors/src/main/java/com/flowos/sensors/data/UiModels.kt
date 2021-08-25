package com.flowos.sensors.data

data class SensorsUiModel(
  val vehicleId: String? = null
)

sealed class SensorsNews {
  object LocationUpdatePublished : SensorsNews()
  object NoDeviceMovement : SensorsNews()
}
