package com.flowos.sensors.useCases

import android.nfc.Tag
import android.nfc.tech.NdefFormatable
import com.flowos.base.interfaces.UseCase
import com.flowos.sensors.data.NfcMeasure
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkStatic
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetPayloadFromNfcMeasureUseCaseTest {

  private lateinit var useCase: UseCase<NfcMeasure, String>

  @BeforeEach
  fun setUp() {
    useCase = GetPayloadFromNfcMeasureUseCase()
  }

  @Test
  fun `Should return a empty payload when messages are null`() {
    // given
    val tag = mockk<Tag>()
    val nfcMeasure = NfcMeasure(tag, null)

    mockkStatic("android.nfc.tech.NdefFormatable")
    every { NdefFormatable.get(any()) } returns mockk(relaxed = true)

    // when
    val payload = useCase.execute(nfcMeasure)

    // then
    assert(payload.isEmpty())
  }
}
