package com.flowos.core.useCases

import android.content.Context
import android.provider.Settings
import com.flowos.base.interfaces.UseCase
import javax.inject.Inject

class GetDeviceIdUseCase @Inject constructor(private val context: Context) :
  UseCase<Unit, String>() {

  override fun execute(input: Unit): String {
    return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
  }
}
