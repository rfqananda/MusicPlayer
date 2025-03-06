package com.example.uicomponent.extension

import android.view.View
import androidx.core.view.isVisible

fun View.visibleIf(visible: Boolean) {
    isVisible = visible
}

fun Int?.orZero() = this ?: 0
