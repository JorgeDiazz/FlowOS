package com.flowos.auth.useCases

import com.flowos.auth.repositories.CredentialsRepository
import com.flowos.base.interfaces.SingleUseCase
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

class IsUserLoggedUseCase @Inject constructor(
  private val credentialsRepository: CredentialsRepository
) : SingleUseCase<Unit, Boolean>() {
  override fun execute(input: Unit): Single<Boolean> {
    return credentialsRepository.isLoggedIn().firstOrError()
  }
}
