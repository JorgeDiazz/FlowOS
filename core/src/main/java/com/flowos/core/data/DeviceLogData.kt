package com.flowos.core.data

data class DeviceLogData(
  val deviceId: String,
  val timestamp: String,
  val tag: String,
  val level: String,
  val message: String,
)
