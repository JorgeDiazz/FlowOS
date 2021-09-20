package com.flowos.sensors.useCases

import com.flowos.base.interfaces.CompletableUseCase
import com.flowos.base.interfaces.Logger
import com.flowos.sensors.entities.DeviceLocationUpdateData
import com.flowos.sensors.repositories.interfaces.DeviceLocationUpdateDataRepository
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class CacheDeviceLocationUpdateDataTemporaryUseCaseTest {

  @MockK(relaxed = true)
  private lateinit var deviceLocationUpdateDataRepository: DeviceLocationUpdateDataRepository

  @MockK(relaxed = true)
  private lateinit var logger: Logger

  private lateinit var useCase: CompletableUseCase<DeviceLocationUpdateData>

  @BeforeEach
  fun setUp() {
    useCase = CacheDeviceLocationUpdateDataTemporaryUseCase(deviceLocationUpdateDataRepository, logger)
  }

  @Test
  fun `Should save DeviceLocationUpdates when use case is invoked`() {
    // given
    val deviceLocationUpdateData = DeviceLocationUpdateData(
      timestamp = "",
      boardId = "",
      coordinate = emptyList(),
      accuracy = 0f,
      bearing = 0f,
      speed = 0f,
      sensors = emptyList()
    )

    // when
    val testObserver = useCase.execute(deviceLocationUpdateData).test()

    // then
    testObserver.assertComplete()
  }
}
