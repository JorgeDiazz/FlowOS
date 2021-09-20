package com.flowos.core.useCases

import com.flowos.base.interfaces.UseCase
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetBleDeviceHashedUseCaseTest {

  private lateinit var useCase: UseCase<String, String>

  @BeforeEach
  fun setUp() {
    useCase = GetBleDeviceHashedUseCase()
  }

  @Test
  fun `Should return ble devices hashed when useCase is executed`() {
    // given
    val bleDeviceMac = "1E:28:C1:B0:3D:DE"
    val expected = "e3387940c00cc1ac903c4bd360a3e9b"

    // when
    val result = useCase.execute(bleDeviceMac)

    // then
    assertEquals(expected, result)
  }
}
