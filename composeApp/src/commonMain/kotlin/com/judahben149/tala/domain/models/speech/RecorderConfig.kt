package com.judahben149.tala.domain.models.speech

data class RecorderConfig(
    val sampleRate: Int = 16_000,
    val channelCount: Int = 1,
    val bitsPerSample: Int = 16,
    val wrapAsWav: Boolean = true
)