package com.flowos.app.viewModels

import com.flowos.app.data.SplashNews
import com.flowos.base.interfaces.Logger
import com.flowos.base.interfaces.SingleUseCase
import com.flowos.core.Event
import com.flowos.core.interfaces.AppResources
import com.flowos.core.test.utils.InstantExecutorExtension
import com.flowos.core.test.utils.RxSchedulerExtension
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.reactivex.rxjava3.core.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(RxSchedulerExtension::class, InstantExecutorExtension::class, MockKExtension::class)
class SplashViewModelTest {

  @MockK(relaxed = true)
  private lateinit var logger: Logger

  @MockK
  private lateinit var resources: AppResources

  @MockK(relaxed = true)
  private lateinit var verifyInternetConnectivityUseCase: SingleUseCase<Unit, Boolean>

  @MockK(relaxed = true)
  private lateinit var isUserLoggedUseCase: SingleUseCase<Unit, Boolean>

  private lateinit var viewModel: SplashViewModel

  @BeforeEach
  fun setUp() {
    viewModel = SplashViewModel(
      logger,
      resources,
      verifyInternetConnectivityUseCase,
      isUserLoggedUseCase,
    )
  }

  @Test
  fun `Should send network connectivity error event when getting no connection exception`() {
    // given
    every { verifyInternetConnectivityUseCase.execute(Unit) } returns Single.just(false)

    // when
    viewModel.onViewActive()

    // then
    assertEquals((viewModel.news.value as Event).peekContent(), SplashNews.ShowNoConnectivityView)
  }
}
