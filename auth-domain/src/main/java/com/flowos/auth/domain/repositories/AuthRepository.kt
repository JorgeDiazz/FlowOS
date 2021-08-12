package com.flowos.auth.domain.repositories

import com.flowos.auth.domain.services.AuthService
import com.flowos.auth.entities.AuthenticateUserRequest
import com.flowos.auth.entities.AuthenticateUserResponse
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class AuthRepository @Inject constructor(
  private val authService: AuthService,
) {
  fun loginUser(
    token: String?,
    request: AuthenticateUserRequest
  ): Single<AuthenticateUserResponse> {
    return authService.login(token, request)
  }
}
