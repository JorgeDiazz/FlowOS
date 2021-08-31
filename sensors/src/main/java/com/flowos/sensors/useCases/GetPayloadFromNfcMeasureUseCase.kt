package com.flowos.sensors.useCases

import android.nfc.NdefMessage
import android.nfc.tech.NdefFormatable
import com.flowos.base.interfaces.UseCase
import com.flowos.sensors.data.NfcMeasure
import javax.inject.Inject

class GetPayloadFromNfcMeasureUseCase @Inject constructor() : UseCase<NfcMeasure, String>() {

  override fun execute(input: NfcMeasure): String {
    val ndef = NdefFormatable.get(input.tag)

    ndef.use {
      it.connect()

      val messages = input.messages

      val ndefMessages = arrayOfNulls<NdefMessage>(messages?.size ?: 0)
      messages?.indices?.forEach { i -> ndefMessages[i] = messages[i] as NdefMessage }

      return if (ndefMessages.isEmpty()) {
        ""
      } else {
        val record = ndefMessages.first()?.records?.first()
        record?.payload.toString()
      }
    }
  }
}
