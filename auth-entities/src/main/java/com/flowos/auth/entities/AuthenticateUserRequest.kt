package com.flowos.auth.entities

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AuthenticateUserRequest(
  @field:Json(name = "driverId") val driverId: String,
  @field:Json(name = "deviceId") val deviceId: String,
  @field:Json(name = "vehicleId") val vehicleId: String? = null,
)
