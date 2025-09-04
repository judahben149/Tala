package com.judahben149.tala.data.mappers

import com.judahben149.tala.data.local.getCurrentTimeMillis
import com.judahben149.tala.data.model.VoiceEntity
import com.judahben149.tala.domain.models.speech.SimpleVoice

fun SimpleVoice.toEntity(): VoiceEntity = VoiceEntity(
    voiceId = voiceId,
    name = name,
    gender = gender,
    category = category,
    description = description,
    previewUrl = previewUrl,
    isOwner = isOwner,
    isFeatured = isFeatured,
    likedCount = likedCount,
    createdAt = getCurrentTimeMillis(),
    updatedAt = getCurrentTimeMillis(),
    isSelected = isSelected
)

fun com.judahben149.tala.Voices.toDomain(): SimpleVoice = SimpleVoice(
    voiceId = voice_id,
    name = name,
    gender = gender,
    category = category,
    description = description,
    previewUrl = preview_url,
    isOwner = is_owner == 1L,
    isFeatured = is_featured == 1L,
    likedCount = liked_count.toInt(),
    isSelected = is_selected == 1L
)