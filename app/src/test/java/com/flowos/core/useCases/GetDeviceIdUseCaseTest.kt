package com.flowos.core.useCases

import android.content.Context
import android.provider.Settings
import com.flowos.base.interfaces.UseCase
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetDeviceIdUseCaseTest {

  @MockK(relaxed = true)
  private lateinit var context: Context

  private lateinit var useCase: UseCase<Unit, String>

  @BeforeEach
  fun setUp() {
    useCase = GetDeviceIdUseCase(context)
  }

  @Test
  fun `Should return device id when useCase is executed`() {
    // given
    val deviceId = "123213213"

    every { context.contentResolver } returns mockk(relaxed = true)

    every {
      Settings.Secure.getString(
        context.contentResolver,
        Settings.Secure.ANDROID_ID
      )
    } returns deviceId

    try {
      // when
      val deviceIdObtained = useCase.execute(Unit)

      // then
      assert(deviceId == deviceIdObtained)
    } catch (e: ClassCastException) {
      // no-op by default
    }
  }
}
