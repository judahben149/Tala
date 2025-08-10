package com.judahben149.tala.util

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect fun getPlatformType(): PlatformType

enum class PlatformType {
    IOS,
    ANDROID
}
