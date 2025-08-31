package com.judahben149.tala.domain.models.haptics

data class HapticSettings(
    val enabled: Boolean = true,
    val intensity: Float = 1.0f, // 0.0 to 1.0
    val enableSystemHaptics: Boolean = true,
    val enableCustomHaptics: Boolean = true
)