package com.judahben149.tala.data.model.network.speech

import com.judahben149.tala.domain.models.speech.CharacterTimestamp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DownloadTtsWithTimestampsResponse(
    @SerialName("audio_base64") val audioBase64: String,
    @SerialName("character_timestamps") val characterTimestamps: List<CharacterTimestamp>? = null,
    @SerialName("normalized_character_timestamps") val normalizedCharacterTimestamps: List<CharacterTimestamp>? = null
)