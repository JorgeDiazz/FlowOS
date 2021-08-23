package com.flowos.base.utils

import java.lang.Enum.valueOf

fun Boolean.toInt() = if (this) 1 else 0

fun Double?.isGreaterThanOrEqualTo(value: Double) = this != null && this >= value

inline fun <reified T : Enum<T>> safeValueOf(type: String?): T? {
  return try {
    type?.let { valueOf(T::class.java, it.replace("-", "_")) }
  } catch (e: IllegalArgumentException) {
    null
  }
}

inline fun <T : Any> multiLet(vararg elements: T?, closure: (List<T>) -> Unit): Unit? {
  return if (elements.all { it != null }) {
    closure(elements.filterNotNull())
  } else {
    null
  }
}
