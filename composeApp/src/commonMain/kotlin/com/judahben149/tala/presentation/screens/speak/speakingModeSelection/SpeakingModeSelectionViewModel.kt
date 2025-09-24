package com.judahben149.tala.presentation.screens.speak.speakingModeSelection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.tala.domain.models.conversation.SpeakingMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SpeakingModeSelectionViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SpeakingModeSelectionUiState())
    val uiState: StateFlow<SpeakingModeSelectionUiState> = _uiState.asStateFlow()

    fun selectSpeakingMode(mode: SpeakingMode) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                selectedMode = mode,
                isProcessingSelection = true
            )
            
            // Add any analytics or logging here
            logModeSelection(mode)
            
            // Reset processing state after selection
            _uiState.value = _uiState.value.copy(
                isProcessingSelection = false
            )
        }
    }

    fun clearSelection() {
        _uiState.value = _uiState.value.copy(
            selectedMode = null,
            isProcessingSelection = false
        )
    }

    private fun logModeSelection(mode: SpeakingMode) {
        // Analytics tracking for mode selection
        when (mode) {
            SpeakingMode.FREE_SPEAK -> {
                // Log free speak selection
            }
            SpeakingMode.GUIDED_PRACTICE -> {
                // Log guided practice selection
            }
        }
    }

    data class SpeakingModeSelectionUiState(
        val isLoading: Boolean = false,
        val selectedMode: SpeakingMode? = null,
        val isProcessingSelection: Boolean = false,
        val error: String? = null
    )
}