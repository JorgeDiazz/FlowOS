package com.flowos.app.useCases

import com.flowos.base.interfaces.SingleUseCase
import com.flowos.core.exceptions.NoConnectionException
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.runs
import okhttp3.OkHttpClient
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class VerifyInternetConnectivityUseCaseTest {

  @MockK(relaxed = true)
  private lateinit var okHttpClient: OkHttpClient

  private lateinit var useCase: SingleUseCase<Unit, Boolean>

  @BeforeEach
  fun setUp() {
    useCase = VerifyInternetConnectivityUseCase(okHttpClient)
  }

  @Test
  fun `Should return true when there is no exception performing the test internet call`() {
    // given
    every { okHttpClient.newCall(any()).execute().close() } just runs

    // when
    val testObserver = useCase.execute(Unit).test()

    // then
    testObserver.assertComplete()
    testObserver.assertValue(true)
  }

  @Test
  fun `Should return false when there is an exception performing the test internet call`() {
    // given
    every { okHttpClient.newCall(any()).execute().close() } throws NoConnectionException()

    // when
    val testObserver = useCase.execute(Unit).test()

    // then
    testObserver.assertComplete()
    testObserver.assertValue(false)
  }
}
