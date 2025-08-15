package com.judahben149.tala.domain.usecases.speech

import com.judahben149.tala.data.model.network.speech.VoiceSettings
import com.judahben149.tala.domain.models.authentication.errors.NetworkException
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.speech.AudioChunk
import com.judahben149.tala.domain.repository.ElevenLabsTtsRepository
import kotlinx.coroutines.flow.Flow

class StreamTextToSpeechUseCase(
    private val repository: ElevenLabsTtsRepository
) {
    
    suspend operator fun invoke(
        text: String,
        voiceId: String,
        apiKey: String,
        voiceSettings: VoiceSettings? = null
    ): Result<Flow<AudioChunk>, NetworkException> {

        if (text.length > 5000) { // ElevenLabs character limit
            return Result.Failure(
                NetworkException.BadRequest("Text exceeds maximum length of 5000 characters")
            )
        }
        
        return repository.streamTextToSpeech(
            text = text,
            voiceId = voiceId,
            apiKey = apiKey,
            voiceSettings = voiceSettings
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
    ): Result<Flow<AudioChunk>, NetworkException> {
        
        val voiceSettings = if (stability != null || similarityBoost != null || 
                              style != null || useSpeakerBoost != null) {
            VoiceSettings(
                stability = stability,
                similarityBoost = similarityBoost,
                style = style,
                useSpeakerBoost = useSpeakerBoost
            )
        } else null
        
        return invoke(text, voiceId, apiKey, voiceSettings)
    }
}
