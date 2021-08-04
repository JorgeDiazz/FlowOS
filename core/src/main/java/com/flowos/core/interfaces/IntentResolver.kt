package com.flowos.core.interfaces

import android.content.Intent

interface IntentResolver {
  fun resolveIntent(host: String): Intent
}
