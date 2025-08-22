package com.judahben149.tala.data.service.audio

import com.judahben149.tala.domain.models.speech.RecorderConfig
import com.judahben149.tala.domain.models.speech.RecorderStatus
import kotlinx.coroutines.flow.StateFlow

interface SpeechRecorder {
    val status: StateFlow<RecorderStatus>

    suspend fun start(config: RecorderConfig = RecorderConfig())

    suspend fun stop(): ByteArray

    suspend fun cancel()
}

