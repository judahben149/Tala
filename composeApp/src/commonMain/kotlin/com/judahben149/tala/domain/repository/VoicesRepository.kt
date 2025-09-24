package com.judahben149.tala.domain.repository

import com.judahben149.tala.domain.models.authentication.errors.NetworkException
import com.judahben149.tala.domain.models.speech.SimpleVoice
import com.judahben149.tala.domain.models.common.Result
import kotlinx.coroutines.flow.Flow

interface VoicesRepository {

    suspend fun getAllVoices(apiKey: String): Result<List<SimpleVoice>, NetworkException>

    suspend fun getFeaturedVoices(apiKey: String): Result<List<SimpleVoice>, NetworkException>

    suspend fun getVoicesByGender(
        apiKey: String,
        gender: String
    ): Result<List<SimpleVoice>, NetworkException>

    suspend fun getVoiceById(voiceId: String): SimpleVoice?
    fun getSelectedVoiceFlow(): Flow<SimpleVoice?>
    suspend fun saveSelectedVoice(voiceId: String)
}