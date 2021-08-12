package com.flowos.auth.entities

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class AuthenticateUserResponse(
  @field:Json(name = "driverName") val driverName: String,
  @field:Json(name = "boards") val boards: List<String>
)
