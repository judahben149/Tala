package com.judahben149.tala.data.repository

import com.judahben149.tala.data.service.speechSynthesis.ElevenLabsService
import com.judahben149.tala.domain.mappers.toNetworkFailure
import com.judahben149.tala.domain.models.authentication.errors.NetworkException
import com.judahben149.tala.domain.models.speech.SimpleVoice
import com.judahben149.tala.domain.models.speech.toSimpleVoice
import com.judahben149.tala.domain.repository.VoicesRepository
import com.judahben149.tala.domain.models.common.Result

class VoicesRepositoryImpl(
    private val service: ElevenLabsService
): VoicesRepository {
    
    override suspend fun getAllVoices(apiKey: String): Result<List<SimpleVoice>, NetworkException> {
        return runCatching {
            val response = service.getVoices(apiKey = apiKey)
            response.voices.map { it.toSimpleVoice() }
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = { it.toNetworkFailure() }
        )
    }
    
    override suspend fun getFeaturedVoices(apiKey: String): Result<List<SimpleVoice>, NetworkException> {
        return runCatching {
            val response = service.getVoices(
                apiKey = apiKey,
                featured = true,
                sort = "name"
            )
            response.voices.map { it.toSimpleVoice() }
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = { it.toNetworkFailure() }
        )
    }
    
    override suspend fun getVoicesByGender(
        apiKey: String, 
        gender: String
    ): Result<List<SimpleVoice>, NetworkException> {
        return runCatching {
            val response = service.getVoices(apiKey = apiKey)
            response.voices
                .map { it.toSimpleVoice() }
                .filter { it.gender?.equals(gender, ignoreCase = true) == true }
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = { it.toNetworkFailure() }
        )
    }
}
