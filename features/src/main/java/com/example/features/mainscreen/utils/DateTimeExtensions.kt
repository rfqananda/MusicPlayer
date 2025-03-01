package com.example.features.mainscreen.utils

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun formatTime(milliseconds: Long): String {
    val totalSeconds = milliseconds / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}
