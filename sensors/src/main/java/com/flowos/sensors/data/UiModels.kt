package com.flowos.sensors.data

data class SensorsUiModel(
  val vehicleId: String? = null,
  val nfcPayload: String? = null,
)

sealed class SensorsNews {
  object LocationUpdatePublished : SensorsNews()
  object NoDeviceMovement : SensorsNews()
  data class ShowErrorNews(val errorMessage: String) : SensorsNews()
}
