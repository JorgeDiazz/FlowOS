package com.flowos.core.useCases

import com.flowos.base.interfaces.CompletableUseCase
import com.flowos.core.data.DeviceLogData
import com.flowos.core.interfaces.DeviceLogDataRepository
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CacheDeviceLogDataTemporaryUseCaseTest {

  @MockK(relaxed = true)
  private lateinit var deviceLogDataRepository: DeviceLogDataRepository

  private lateinit var useCase: CompletableUseCase<DeviceLogData>

  @BeforeEach
  fun setUp() {
    useCase = CacheDeviceLogDataTemporaryUseCase(deviceLogDataRepository)
  }

  @Test
  fun `Should save DeviceLogData when use case is invoked`() {
    // given
    val deviceLogData = DeviceLogData(
      deviceId = "",
      timestamp = "",
      tag = "",
      level = "",
      message = ""
    )

    // when
    val testObserver = useCase.execute(deviceLogData).test()

    // then
    testObserver.assertComplete()
  }
}
