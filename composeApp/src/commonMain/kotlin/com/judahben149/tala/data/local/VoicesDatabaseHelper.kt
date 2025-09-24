package com.judahben149.tala.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.judahben149.tala.TalaDatabase
import com.judahben149.tala.data.mappers.toDomain
import com.judahben149.tala.data.model.VoiceEntity
import com.judahben149.tala.domain.models.speech.SimpleVoice
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VoicesDatabaseHelper(driverFactory: DatabaseDriverFactory) {
    private val database: TalaDatabase = TalaDatabase(driverFactory.createDriver())
    private val voicesQueries = database.voicesQueries

    fun getAllVoicesFlow(): Flow<List<SimpleVoice>> {
        return voicesQueries.getAllVoices()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { voices -> voices.map { it.toDomain() } }
    }

    fun getSelectedVoiceFlow(): Flow<SimpleVoice?> {
        return voicesQueries.getSelectedVoice()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { it.firstOrNull()?.toDomain() }
    }

    suspend fun saveSelectedVoice(voiceId: String) {
        database.transaction {
            voicesQueries.deselectAllVoices()
            voicesQueries.selectVoiceById(voiceId)
        }
    }

    suspend fun getAllVoices(): List<SimpleVoice> {
        return voicesQueries.getAllVoices().executeAsList().map { it.toDomain() }
    }

    suspend fun getFeaturedVoices(): List<SimpleVoice> {
        return voicesQueries.getFeaturedVoices().executeAsList().map { it.toDomain() }
    }

    suspend fun getVoicesByGender(gender: String): List<SimpleVoice> {
        return voicesQueries.getVoicesByGender(gender).executeAsList().map { it.toDomain() }
    }

    suspend fun insertVoices(voices: List<VoiceEntity>) {
        database.transaction {
            voicesQueries.deleteAllVoices()
            voices.forEach { voice ->
                voicesQueries.insertVoice(
                    voice_id = voice.voiceId,
                    name = voice.name,
                    gender = voice.gender,
                    category = voice.category,
                    description = voice.description,
                    preview_url = voice.previewUrl,
                    is_owner = if (voice.isOwner) 1L else 0L,
                    is_featured = if (voice.isFeatured) 1L else 0L,
                    liked_count = voice.likedCount.toLong(),
                    created_at = voice.createdAt,
                    updated_at = voice.updatedAt,
                    is_selected = if (voice.isSelected) 1L else 0L
                )
            }
        }
    }

    suspend fun updateCacheMetadata(timestamp: Long, count: Int) {
        voicesQueries.updateCacheMetadata(timestamp, count.toLong())
    }

    suspend fun getCacheTimestamp(): Long? {
        return voicesQueries.getCacheMetadata().executeAsOneOrNull()?.last_fetched
    }

    suspend fun getVoiceById(voiceId: String): SimpleVoice? {
        return voicesQueries.getVoiceById(voiceId).executeAsOneOrNull()?.toDomain()
    }
}
