package com.judahben149.tala.data.repository

import com.judahben149.tala.data.service.speechSynthesis.ElevenLabsService
import com.judahben149.tala.domain.mappers.toNetworkFailure
import com.judahben149.tala.domain.models.authentication.errors.NetworkException
import com.judahben149.tala.domain.models.speech.SimpleVoice
import com.judahben149.tala.domain.models.speech.toSimpleVoice
import com.judahben149.tala.domain.repository.VoicesRepository
import com.judahben149.tala.domain.models.common.Result
import co.touchlab.kermit.Logger
import com.judahben149.tala.data.local.VoicesDatabaseHelper
import com.judahben149.tala.data.local.getCurrentTimeMillis
import com.judahben149.tala.data.mappers.toEntity
import kotlinx.coroutines.flow.Flow

class VoicesRepositoryImpl(
    private val service: ElevenLabsService,
    private val databaseHelper: VoicesDatabaseHelper,
    private val logger: Logger
): VoicesRepository {

    private val cacheValidityHours = 24L

    override suspend fun getAllVoices(apiKey: String): Result<List<SimpleVoice>, NetworkException> {
        return try {
            // Try to get from cache first
            val cachedVoices = databaseHelper.getAllVoices()
            val cacheTimestamp = databaseHelper.getCacheTimestamp()
            val currentTime = getCurrentTimeMillis()
            val cacheAge = currentTime - (cacheTimestamp ?: 0)
            val cacheValidityMs = cacheValidityHours * 60 * 60 * 1000

            // Return cached data if valid and not empty
            if (cachedVoices.isNotEmpty() && cacheAge < cacheValidityMs) {
                logger.d { "Returning cached voices: ${cachedVoices.size}" }
                Result.Success(cachedVoices)
            } else {
                // Fetch fresh data
                logger.d { "Fetching fresh voices from network" }
                fetchAndCacheVoices(apiKey)
            }
        } catch (e: Exception) {
            // Fallback to cache if network fails
            val cachedVoices = databaseHelper.getAllVoices()
            if (cachedVoices.isNotEmpty()) {
                logger.w { "Network failed, using cached voices: ${cachedVoices.size}" }
                Result.Success(cachedVoices)
            } else {
                logger.e(e) { "Failed to get voices and no cache available" }
                e.toNetworkFailure()
            }
        }
    }

    private suspend fun fetchAndCacheVoices(apiKey: String): Result<List<SimpleVoice>, NetworkException> {
        return runCatching {
            val response = service.getVoices(apiKey = apiKey, pageSize = 100)
            val voices = response.voices.map { it.toSimpleVoice() }

            // Cache the voices
            databaseHelper.insertVoices(voices.map { it.toEntity() })
            databaseHelper.updateCacheMetadata(getCurrentTimeMillis(), voices.size)

            logger.d { "Cached ${voices.size} voices" }
            voices
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = { it.toNetworkFailure() }
        )
    }

    override suspend fun getFeaturedVoices(apiKey: String): Result<List<SimpleVoice>, NetworkException> {
        when (val result = getAllVoices(apiKey)) {
            is Result.Success -> {
//                val featuredVoices = databaseHelper.getFeaturedVoices()
                val allVoices = databaseHelper.getAllVoices()

                val sortedVoices = allVoices
                    .filter { it.description != null && it.description.isNotBlank() }
                    .sortedBy { it.description?.length }

                val maleVoices = sortedVoices.filter { it.gender.equals("male", ignoreCase = true) }.take(3)
                val femaleVoices = sortedVoices.filter { it.gender.equals("female", ignoreCase = true) }.take(3)

                val selectedVoices = (maleVoices + femaleVoices).distinctBy { it.voiceId }

                logger.d { "Returning ${selectedVoices.size} voices after filtering and sorting" }
                return Result.Success(selectedVoices)
            }
            is Result.Failure -> return result
        }
    }

    override suspend fun getVoicesByGender(
        apiKey: String,
        gender: String
    ): Result<List<SimpleVoice>, NetworkException> {
        when (val result = getAllVoices(apiKey)) {
            is Result.Success -> {
                val genderVoices = databaseHelper.getVoicesByGender(gender)
                return Result.Success(genderVoices)
            }
            is Result.Failure -> return result
        }
    }

    fun getAllVoicesFlow(): Flow<List<SimpleVoice>> {
        return databaseHelper.getAllVoicesFlow()
    }

    override suspend fun getVoiceById(voiceId: String): SimpleVoice? {
        return databaseHelper.getVoiceById(voiceId)
    }
}

