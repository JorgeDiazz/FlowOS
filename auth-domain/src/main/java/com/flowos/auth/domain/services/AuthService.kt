package com.flowos.auth.domain.services

import com.flowos.auth.entities.AuthenticateUserRequest
import com.flowos.auth.entities.AuthenticateUserResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthService {
  @POST("login")
  fun login(
    @Header("token") token: String?,
    @Body request: AuthenticateUserRequest
  ): Single<AuthenticateUserResponse>
}
