package com.judahben149.tala.domain.usecases.speech

import com.judahben149.tala.BuildKonfig
import com.judahben149.tala.data.model.network.speech.DownloadTtsWithTimestampsResponse
import com.judahben149.tala.data.model.network.speech.VoiceSettings
import com.judahben149.tala.domain.managers.RemoteConfigManager
import com.judahben149.tala.domain.models.authentication.errors.NetworkException
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.speech.SpeechModel
import com.judahben149.tala.domain.repository.ElevenLabsTtsRepository

class DownloadTextToSpeechUseCase(
    private val repository: ElevenLabsTtsRepository,
    private val remoteConfigManager: RemoteConfigManager
) {

    suspend operator fun invoke(
        text: String,
        voiceId: String,
        voiceSettings: VoiceSettings? = null
    ): Result<DownloadTtsWithTimestampsResponse, NetworkException> {

        if (text.length > 5000) { // ElevenLabs character limit
            return Result.Failure(
                NetworkException.BadRequest("Text exceeds maximum length of 5000 characters")
            )
        }

        val apiKey = remoteConfigManager.getString("eleven_labs_api_key", BuildKonfig.ELEVEN_LABS_API_KEY)
        println("[DEBUG_LOG] Using API key: ${apiKey.take(10)}... for text-to-speech")
        println("[DEBUG_LOG] Using model: ${SpeechModel.ELEVEN_TURBO_V2_5.modelId}")

        return repository.downloadTextToSpeech(
            text = text,
            voiceId = voiceId,
            apiKey = apiKey,
            model = SpeechModel.ELEVEN_TURBO_V2_5,
            voiceSettings = voiceSettings,
            outputFormat = "mp3_44100_128"
        )
    }

    suspend operator fun invoke(
        text: String,
        voiceId: String,
        apiKey: String,
        stability: Float? = null,
        similarityBoost: Float? = null,
        style: Float? = null,
        useSpeakerBoost: Boolean? = null
    ): Result<DownloadTtsWithTimestampsResponse, NetworkException> {

        val voiceSettings = if (stability != null || similarityBoost != null || 
                              style != null || useSpeakerBoost != null) {
            VoiceSettings(
                stability = stability,
                similarityBoost = similarityBoost,
                style = style,
                useSpeakerBoost = useSpeakerBoost
            )
        } else null

        return invoke(
            text = text,
            voiceId = voiceId,
            voiceSettings = voiceSettings
        )
    }
}
