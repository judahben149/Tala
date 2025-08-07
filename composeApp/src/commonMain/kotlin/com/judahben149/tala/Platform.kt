package com.judahben149.tala

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform