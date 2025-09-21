package com.judahben149.tala.domain.usecases.speech

import com.judahben149.tala.BuildKonfig
import com.judahben149.tala.data.model.network.speech.SpeechToTextResponse
import com.judahben149.tala.domain.managers.RemoteConfigManager
import com.judahben149.tala.domain.models.authentication.errors.NetworkException
import com.judahben149.tala.domain.repository.ElevenLabsTtsRepository
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.user.Language

class ConvertSpeechToTextUseCase(
    private val repository: ElevenLabsTtsRepository,
    private val remoteConfigManager: RemoteConfigManager
) {
    suspend operator fun invoke(
        audioBytes: ByteArray,
        fileName: String = "recording.wav",
        mimeType: String = "audio/wav",
        language: Language,
    ): Result<SpeechToTextResponse, NetworkException> {
        return repository.speechToText(
            apiKey = remoteConfigManager.getString("eleven_labs_api_key" , BuildKonfig.ELEVEN_LABS_API_KEY),
            audioBytes = audioBytes,
            fileName = fileName,
            mimeType = mimeType,
            language = language
        ).also {
            println("Result: $it")
        }
    }
}
