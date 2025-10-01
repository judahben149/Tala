package com.judahben149.tala.util

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@OptIn(ExperimentalForeignApi::class)
actual object BrowserUtil {
    actual fun openUrl(url: String) {
        try {
            NSURL.URLWithString(url)?.let { nsUrl ->
                UIApplication.sharedApplication.openURL(
                    nsUrl,
                    emptyMap<Any?, Any>(),
                    null
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}