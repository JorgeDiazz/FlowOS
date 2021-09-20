package com.flowos.core.useCases

import com.flowos.base.interfaces.UseCase
import java.math.BigInteger
import java.security.MessageDigest
import javax.inject.Inject

private const val HASH_ALGORITHM = "MD5"

class GetBleDeviceHashedUseCase @Inject constructor() : UseCase<String, String>() {

  override fun execute(input: String): String {
    val messageDigest = MessageDigest.getInstance(HASH_ALGORITHM)
      .apply { update(input.toByteArray(), 0, input.length) }

    return BigInteger(1, messageDigest.digest()).toString(16)
  }
}
