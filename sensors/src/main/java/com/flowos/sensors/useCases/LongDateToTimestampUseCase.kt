package com.flowos.sensors.useCases

import com.flowos.base.interfaces.UseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

private const val DEFAULT_TIMEZONE = "UTC"
private const val DATE_FORMAT_ISO_8601 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

class LongDateToTimestampUseCase @Inject constructor() : UseCase<Long, String>() {

  override fun execute(input: Long): String {
    return SimpleDateFormat(DATE_FORMAT_ISO_8601, Locale.ENGLISH).apply {
      timeZone = TimeZone.getTimeZone(DEFAULT_TIMEZONE)
    }.format(Date(input))
  }
}
