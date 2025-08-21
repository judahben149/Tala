package com.judahben149.tala.domain.repository

import com.judahben149.tala.data.model.network.speech.DownloadTtsWithTimestampsResponse
import com.judahben149.tala.data.model.network.speech.VoiceSettings
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.authentication.errors.NetworkException
import com.judahben149.tala.domain.models.speech.AudioChunk
import com.judahben149.tala.domain.models.speech.SpeechModel
import kotlinx.coroutines.flow.Flow

interface ElevenLabsTtsRepository {
    suspend fun streamTextToSpeech(
        text: String,
        voiceId: String,
        apiKey: String,
        model: SpeechModel = SpeechModel.ELEVEN_TURBO_V2_5,
        voiceSettings: VoiceSettings? = null,
        outputFormat: String = "mp3_44100_128"
    ): Result<Flow<AudioChunk>, NetworkException>

    suspend fun downloadTextToSpeech(
        text: String,
        voiceId: String,
        apiKey: String,
        model: SpeechModel = SpeechModel.ELEVEN_TURBO_V2_5,
        voiceSettings: VoiceSettings? = null,
        outputFormat: String = "mp3_44100_128"
    ): Result<DownloadTtsWithTimestampsResponse, NetworkException>
}