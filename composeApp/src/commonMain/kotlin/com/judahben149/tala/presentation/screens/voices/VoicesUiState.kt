package com.judahben149.tala.presentation.screens.voices

import com.judahben149.tala.domain.models.speech.SimpleVoice

data class VoicesUiState(
    val voices: List<SimpleVoice> = emptyList(),
    val selectedIndex: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isPlayingSample: Boolean = false
)