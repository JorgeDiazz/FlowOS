package com.flowos.auth.domain.data

class LoginData(
  val driverId: String,
  val deviceId: String,
  val vehicleId: String? = null,
)
