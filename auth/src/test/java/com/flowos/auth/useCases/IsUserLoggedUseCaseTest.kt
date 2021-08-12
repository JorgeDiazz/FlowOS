package com.flowos.auth.useCases

import com.flowos.auth.repositories.CredentialsRepository
import com.flowos.base.interfaces.SingleUseCase
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.reactivex.rxjava3.core.Observable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class IsUserLoggedUseCaseTest {

  @MockK(relaxed = true)
  private lateinit var credentialsRepository: CredentialsRepository

  private lateinit var useCase: SingleUseCase<Unit, Boolean>

  @BeforeEach
  fun setUp() {
    useCase = IsUserLoggedUseCase(credentialsRepository)
  }

  @Test
  fun `Should return if user is logged in when useCase is executed`() {
    // given
    every { credentialsRepository.isLoggedIn() } returns Observable.just(true)

    // when
    val testObserver = useCase.execute(Unit).test()

    // then
    testObserver.assertValue(true)
  }
}
