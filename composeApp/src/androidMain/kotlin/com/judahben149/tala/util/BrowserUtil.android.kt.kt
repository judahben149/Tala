package com.judahben149.tala.util

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity

@SuppressLint("StaticFieldLeak")
actual object BrowserUtil {
    private var context: Context? = null

    fun init(appContext: Context) {
        context = appContext.applicationContext
    }

    actual fun openUrl(url: String) {
        context?.let { ctx ->
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                ctx.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}