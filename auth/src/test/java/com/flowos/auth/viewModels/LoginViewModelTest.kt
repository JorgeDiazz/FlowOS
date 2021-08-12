package com.flowos.auth.viewModels

import com.flowos.auth.data.LoginNews
import com.flowos.auth.domain.data.DriverData
import com.flowos.auth.domain.data.LoginData
import com.flowos.base.interfaces.Logger
import com.flowos.base.interfaces.SingleUseCase
import com.flowos.base.interfaces.UseCase
import com.flowos.core.test.utils.InstantExecutorExtension
import com.flowos.core.test.utils.RxSchedulerExtension
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.reactivex.rxjava3.core.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(RxSchedulerExtension::class, InstantExecutorExtension::class, MockKExtension::class)
class LoginViewModelTest {

  @MockK(relaxed = true)
  private lateinit var logger: Logger

  @MockK(relaxed = true)
  private lateinit var loginUserUseCase: SingleUseCase<Pair<LoginData, String?>, DriverData>

  @MockK(relaxed = true)
  private lateinit var getDeviceIdUseCase: UseCase<Unit, String>

  private lateinit var viewModel: LoginViewModel

  @BeforeEach
  fun setUp() {
    viewModel = LoginViewModel(
      logger,
      loginUserUseCase,
      getDeviceIdUseCase
    )
  }

  @Test
  fun `Should send LoginSuccessful event when it's possible to login`() {
    // given
    val driverId = "123456"
    val deviceId = "123213213"
    val driverData = DriverData(
      driverName = "Jane Smith",
      boards = listOf("1234", "abcd"),
    )

    every { getDeviceIdUseCase.execute(Unit) } returns deviceId

    every { loginUserUseCase.execute(any()) } returns Single.just(driverData)

    // when
    viewModel.loginUser(driverId)

    // then
    assert(viewModel.news.value!!.peekContent() is LoginNews.LoginSuccessful)
  }
}
