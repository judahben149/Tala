package com.judahben149.tala.domain.models.speech

data class AudioChunk(
    val audioData: ByteArray,
    val timestamps: List<CharacterTimestamp>
)