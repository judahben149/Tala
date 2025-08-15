package com.judahben149.tala.domain.models.speech

import com.judahben149.tala.data.model.network.speech.Voice
import com.judahben149.tala.data.model.network.speech.VoiceSettings
import kotlinx.serialization.Serializable

@Serializable
data class SimpleVoice(
    val voiceId: String,
    val name: String,
    val gender: String? = null,
    val category: String,
    val description: String? = null,
    val previewUrl: String? = null,
    val isOwner: Boolean = false,
    val isFeatured: Boolean = false,
    val likedCount: Int = 0,
    val settings: VoiceSettings? = null
)


fun Voice.toSimpleVoice(): SimpleVoice {
    return SimpleVoice(
        voiceId = voiceId,
        name = name,
        gender = labels?.get("gender") ?: labels?.get("Gender"),
        category = category,
        description = description,
        previewUrl = previewUrl,
        isOwner = isOwner ?: false,
        isFeatured = sharing?.featured ?: false,
        likedCount = sharing?.likedByCount ?: 0,
        settings = settings
    )
}
