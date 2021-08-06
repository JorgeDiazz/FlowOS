package com.flowos.app.data

import android.content.Intent
import android.net.Uri
import com.flowos.app.BuildConfig
import com.flowos.core.interfaces.IntentResolver

class ResolveIntentImpl : IntentResolver {
  override fun resolveIntent(host: String): Intent {
    val url = "${BuildConfig.SCHEME}://$host"
    return Intent(Intent.ACTION_VIEW, Uri.parse(url))
  }
}
