package com.judahben149.tala.util

class AndroidPlatform: Platform {
    override val name: String = "Android ${android.os.Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

actual fun getPlatformType(): PlatformType = PlatformType.ANDROID