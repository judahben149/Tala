package com.judahben149.tala.domain.models.speech

import kotlinx.serialization.Serializable

@Serializable
data class CharacterTimestamp(
    val character: String,
    val startTimeS: Double,
    val endTimeS: Double
)