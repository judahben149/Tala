package com.judahben149.tala.presentation.screens.signUp.language

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.user.Language
import com.judahben149.tala.domain.usecases.preferences.SaveLearningLanguageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LanguageSelectionViewModel(
    private val saveLearningLanguageUseCase: SaveLearningLanguageUseCase,
    private val logger: Logger
) : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageSelectionState())
    val uiState: StateFlow<LanguageSelectionState> = _uiState.asStateFlow()

    fun loadLanguages() {
        val languages = listOf(
            Language.SPANISH,
            Language.FRENCH,
            Language.GERMAN,
            Language.ITALIAN,
            Language.JAPANESE,
            Language.KOREAN,
            Language.MANDARIN
        )
        
        _uiState.update {
            it.copy(
                availableLanguages = languages,
                selectedLanguage = Language.SPANISH // Default selection
            )
        }
    }

    fun selectLanguage(language: Language) {
        _uiState.update { it.copy(selectedLanguage = language) }
    }

    fun saveSelectedLanguage() {
        val selectedLanguage = _uiState.value.selectedLanguage ?: return
        
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            when (val result = saveLearningLanguageUseCase(selectedLanguage.name)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    logger.d { "Language saved: ${selectedLanguage.name}" }
                }
                is Result.Failure -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to save language: ${result.error.message}"
                        )
                    }
                    logger.e { "Failed to save language: ${result.error}" }
                }
            }
        }
    }
}

data class LanguageSelectionState(
    val availableLanguages: List<Language> = emptyList(),
    val selectedLanguage: Language? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
