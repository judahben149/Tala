package com.judahben149.tala.domain.models.conversation

import kotlinx.serialization.Serializable

@Serializable
enum class SpeakingMode {
    FREE_SPEAK,
    GUIDED_PRACTICE
}