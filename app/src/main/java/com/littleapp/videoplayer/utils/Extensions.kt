package com.littleapp.videoplayer.utils

import android.content.Context
import android.content.Intent
import java.util.Locale

fun Context.intent1(cls: Class<*>, init: Intent.() -> Unit = {}) {
    val intent = Intent(this, cls)
    intent.init()
    startActivity(intent)
}

fun Long.formatDuration(): String {
    val minutes = this / 1000 / 60
    val seconds = this / 1000 % 60
    return String.format(Locale.US, "%d:%02d", minutes, seconds)
}