package com.flowos.app.useCases

import com.flowos.base.interfaces.SingleUseCase
import com.flowos.core.di.OkHttpClientBasic
import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import okhttp3.Request
import javax.inject.Inject

private const val SETTINGS_HOST = "https://www.google.com"

class VerifyInternetConnectivityUseCase @Inject constructor(
  @OkHttpClientBasic private val okHttpClient: OkHttpClient
) : SingleUseCase<Unit, Boolean>() {
  override fun execute(input: Unit): Single<Boolean> {
    return checkInternetConnectivity()
  }

  private fun checkInternetConnectivity(): Single<Boolean> {
    return Single.create { emitter ->
      var hasInternetConnectivity = true

      try {
        val request = Request.Builder()
          .url(SETTINGS_HOST)
          .build()

        okHttpClient.newCall(request).execute().close()
      } catch (e: Exception) {
        e.printStackTrace()
        hasInternetConnectivity = false
      }

      emitter.onSuccess(hasInternetConnectivity)
    }
  }
}
