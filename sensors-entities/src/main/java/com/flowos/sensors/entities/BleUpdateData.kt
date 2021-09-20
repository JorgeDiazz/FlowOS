package com.flowos.sensors.entities

data class BleUpdateData(
  val timestamp: String,
  val added: List<String>,
  val removed: List<String>,
)
