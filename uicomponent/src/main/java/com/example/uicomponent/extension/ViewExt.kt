package com.example.uicomponent.extension

import android.view.View
import androidx.core.view.isVisible

fun View.visibleIf(visible: Boolean) {
    isVisible = visible
}

fun View.invisibleIf(invisible: Boolean) {
    visibility = if (invisible) View.INVISIBLE else View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun Int?.orZero() = this ?: 0
