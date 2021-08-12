package com.flowos.auth.domain.useCases

import com.flowos.auth.domain.data.DriverData
import com.flowos.auth.domain.data.LoginData
import com.flowos.auth.domain.repositories.AuthRepository
import com.flowos.auth.entities.AuthenticateUserResponse
import com.flowos.base.interfaces.SingleUseCase
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.reactivex.rxjava3.core.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class LoginUserUseCaseTest {

  @MockK(relaxed = true)
  private lateinit var authRepository: AuthRepository

  private lateinit var useCase: SingleUseCase<Pair<LoginData, String?>, DriverData>

  @BeforeEach
  fun setUp() {
    useCase = LoginUserUseCase(authRepository)
  }

  @Test
  fun `Should return DriveData dto when user is logged in`() {
    // given
    val token = "******"
    val loginData = LoginData(
      driverId = "#12345",
      deviceId = "a123rfsf4-asdg421",
      vehicleId = "1",
    )

    val authenticateUserResponse = AuthenticateUserResponse(
      driverName = "Jane Smith",
      boards = listOf("1234", "abcd"),
    )

    every { authRepository.loginUser(token, any()) } returns Single.just(
      authenticateUserResponse
    )

    val driverData = DriverData(
      driverName = authenticateUserResponse.driverName,
      boards = authenticateUserResponse.boards
    )

    // when
    val testObserver = useCase.execute(loginData to token).test()

    // then
    testObserver.assertValue { it.driverName == driverData.driverName && it.boards == driverData.boards }
  }
}
