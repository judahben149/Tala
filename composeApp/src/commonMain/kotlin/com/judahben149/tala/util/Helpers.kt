package com.judahben149.tala.util

fun isIos(): Boolean {
    return getPlatformType() == PlatformType.IOS
}