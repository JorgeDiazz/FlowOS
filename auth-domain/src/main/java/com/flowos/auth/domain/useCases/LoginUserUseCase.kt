package com.flowos.auth.domain.useCases

import com.flowos.auth.domain.data.DriverData
import com.flowos.auth.domain.data.LoginData
import com.flowos.auth.domain.repositories.AuthRepository
import com.flowos.auth.entities.AuthenticateUserRequest
import com.flowos.base.interfaces.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
  private val authRepository: AuthRepository
) :
  SingleUseCase<Pair<@JvmSuppressWildcards LoginData, @JvmSuppressWildcards String?>, DriverData>() {

  override fun execute(input: Pair<LoginData, String?>): Single<DriverData> {
    return authRepository.loginUser(
      token = input.second,
      AuthenticateUserRequest(
        input.first.driverId,
        input.first.deviceId,
        input.first.vehicleId,
      )
    ).map {
      DriverData(driverName = it.driverName, boards = it.boards)
    }
  }
}
