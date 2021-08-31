package com.flowos.sensors.data

import android.nfc.Tag
import android.os.Parcelable

data class NfcMeasure(
  val tag: Tag?,
  val messages: Array<Parcelable>?,
)
