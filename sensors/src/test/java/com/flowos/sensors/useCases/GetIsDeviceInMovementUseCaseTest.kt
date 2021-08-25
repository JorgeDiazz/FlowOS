package com.flowos.sensors.useCases

import com.flowos.base.interfaces.UseCase
import com.flowos.sensors.entities.SensorMeasure
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetIsDeviceInMovementUseCaseTest {

  private lateinit var useCase: UseCase<SensorMeasure, Boolean>

  @BeforeEach
  fun setUp() {
    useCase = GetIsDeviceInMovementUseCase()
  }

  @Test
  fun `Should return true when accelerometer SensorMeasure distance is higher than useCase threshold`() {
    // given
    val sensorMeasure = SensorMeasure(
      sensor = "A",
      longTimestamp = 1629409063000L,
      timestamp = "2021-08-19T21:37:43.000Z",
      values = floatArrayOf(-23f, 9.78f, 0.81f)
    )

    // when
    val result = useCase.execute(sensorMeasure)

    // then
    assert(result)
  }

  @Test
  fun `Should return false when accelerometer SensorMeasure distance is lower than useCase threshold`() {
    // given
    val sensorMeasure = SensorMeasure(
      sensor = "A",
      longTimestamp = 1629409063000L,
      timestamp = "2021-08-19T21:37:43.000Z",
      values = floatArrayOf(0f, 0f, 0f)
    )

    // when
    val result = useCase.execute(sensorMeasure)

    // then
    assert(!result)
  }
}
