package com.flowos.sensors.entities

data class DeviceStateUpdateData(
  val timestamp: String,
  val boardId: String,
  val driverId: String,
  val vehicleId: String,
  val plugged: Boolean,
  val batteryLevel: Int,
)
