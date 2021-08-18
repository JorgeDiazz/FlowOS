package com.flowos.components.views

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.flowos.components.R

class LoadingView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

  init {
    inflate(context, R.layout.progress_bar, this)
    setBackgroundColor(ContextCompat.getColor(context, R.color.grey_silver))
    isClickable = true
    isFocusable = true
  }
}
