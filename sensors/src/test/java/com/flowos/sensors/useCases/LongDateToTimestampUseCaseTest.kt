package com.flowos.sensors.useCases

import com.flowos.base.interfaces.UseCase
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class LongDateToTimestampUseCaseTest {

  private lateinit var useCase: UseCase<Long, String>

  @BeforeEach
  fun setUp() {
    useCase = LongDateToTimestampUseCase()
  }

  @Test
  fun `Should return timestamp when useCase is executed`() {
    // given
    val date = 1629409063000L

    // when
    val result = useCase.execute(date)

    // then
    assert(result == "2021-08-19T21:37:43.000Z")
  }
}
