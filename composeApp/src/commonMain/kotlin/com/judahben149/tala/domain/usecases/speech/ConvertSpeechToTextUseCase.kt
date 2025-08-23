package com.judahben149.tala.domain.usecases.speech

import com.judahben149.tala.BuildKonfig
import com.judahben149.tala.data.model.network.speech.SpeechToTextResponse
import com.judahben149.tala.domain.models.authentication.errors.NetworkException
import com.judahben149.tala.domain.repository.ElevenLabsTtsRepository
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.language.Language

class ConvertSpeechToTextUseCase(
    private val repository: ElevenLabsTtsRepository
) {
    suspend operator fun invoke(
        audioBytes: ByteArray,
        fileName: String = "recording.wav",
        mimeType: String = "audio/wav",
        language: Language,
    ): Result<SpeechToTextResponse, NetworkException> {
        return repository.speechToText(
            apiKey = BuildKonfig.ELEVEN_LABS_API_KEY,
            audioBytes = audioBytes,
            fileName = fileName,
            mimeType = mimeType,
            language = language
        ).also {
            println("Result: $it")
        }
    }
}
