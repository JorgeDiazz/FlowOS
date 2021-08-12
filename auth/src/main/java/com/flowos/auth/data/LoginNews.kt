package com.flowos.auth.data

import com.flowos.auth.domain.data.DriverData

sealed class LoginNews {
  data class LoginSuccessful(val driverData: DriverData) : LoginNews()
  data class ShowErrorNews(val message: String) : LoginNews()
}
