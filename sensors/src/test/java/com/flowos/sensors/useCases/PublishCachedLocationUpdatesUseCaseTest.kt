package com.flowos.sensors.useCases

import com.flowos.base.interfaces.CompletableUseCase
import com.flowos.sensors.entities.DeviceLocationUpdateData
import com.flowos.sensors.repositories.interfaces.DeviceLocationUpdateDataRepository
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import io.reactivex.rxjava3.core.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class PublishCachedLocationUpdatesUseCaseTest {

  @MockK(relaxed = true)
  private lateinit var deviceLocationUpdateDataRepository: DeviceLocationUpdateDataRepository

  private lateinit var useCase: CompletableUseCase<Unit>

  @BeforeEach
  fun setUp() {
    useCase = PublishCachedLocationUpdatesUseCase(deviceLocationUpdateDataRepository)
  }

  @Test
  fun `Should invoke getDeviceLocationUpdates function when use case is invoked`() {
    // when
    useCase.execute(Unit).test()

    // then
    verify(exactly = 1) { deviceLocationUpdateDataRepository.getDeviceLocationUpdates() }
  }

  @Test
  fun `Should clear DeviceLocationUpdates information when use case is invoked`() {
    // given
    val deviceLocationUpdates = listOf(
      DeviceLocationUpdateData(
        timestamp = "",
        boardId = "",
        coordinate = emptyList(),
        accuracy = 0f,
        bearing = 0f,
        speed = 0f,
        sensors = emptyList()
      )
    )

    every { deviceLocationUpdateDataRepository.getDeviceLocationUpdates() } returns Single.just(deviceLocationUpdates)

    // when
    useCase.execute(Unit).test()

    // then
    verify(exactly = 1) { deviceLocationUpdateDataRepository.clearDeviceLocationUpdates() }
  }
}
