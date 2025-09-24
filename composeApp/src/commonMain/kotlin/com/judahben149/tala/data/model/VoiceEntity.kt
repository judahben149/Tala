package com.judahben149.tala.data.model

data class VoiceEntity(
    val voiceId: String,
    val name: String,
    val gender: String?,
    val category: String,
    val description: String?,
    val previewUrl: String?,
    val isOwner: Boolean,
    val isFeatured: Boolean,
    val likedCount: Int,
    val createdAt: Long,
    val updatedAt: Long,
    val isSelected: Boolean = false
)
