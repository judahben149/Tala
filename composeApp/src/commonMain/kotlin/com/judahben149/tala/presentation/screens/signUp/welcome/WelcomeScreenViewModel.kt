package com.judahben149.tala.presentation.screens.signUp.welcome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.judahben149.tala.data.service.SignInStateTracker
import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.user.Language
import com.judahben149.tala.domain.usecases.authentication.GetCurrentUserUseCase
import com.judahben149.tala.domain.usecases.settings.GetLearningLanguageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WelcomeScreenViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getLearningLanguageUseCase: GetLearningLanguageUseCase,
    private val signInStateTracker: SignInStateTracker,
    private val sessionManager: SessionManager,
    private val logger: Logger
) : ViewModel() {

    private val _uiState = MutableStateFlow(WelcomeScreenState())
    val uiState: StateFlow<WelcomeScreenState> = _uiState.asStateFlow()

    fun loadUserData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Get current user
                val currentUser = getCurrentUserUseCase()

                val userName = when (currentUser) {
                    is Result.Success -> currentUser.data?.displayName ?: "Friend"
                    is Result.Failure -> "Friend"
                }
                
                // Get selected language
                val languageResult = getLearningLanguageUseCase()

                val selectedLanguage = when (languageResult) {
                    is Result.Success -> languageResult.data
                    is Result.Failure -> Language.ENGLISH
                }

                val selectedInterests = sessionManager.getUserInterests().toList()

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userName = userName,
                        selectedLanguage = selectedLanguage.name,
                        selectedInterests = selectedInterests
                    )
                }
                
            } catch (e: Exception) {
                logger.e(e) { "Failed to load user data" }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        userName = "Friend",
                        selectedLanguage = Language.ENGLISH.name,
                        selectedInterests = emptyList()
                    )
                }
            }
        }
    }
    
    fun completeOnboarding() {
        viewModelScope.launch {
            try {
                signInStateTracker.markOnboardingCompleted()
                logger.d { "Onboarding completed successfully" }
            } catch (e: Exception) {
                logger.e(e) { "Failed to mark onboarding as completed" }
            }
        }
    }
}

data class WelcomeScreenState(
    val isLoading: Boolean = false,
    val userName: String = "",
    val selectedLanguage: String = Language.ENGLISH.name,
    val selectedInterests: List<String> = emptyList(),
    val error: String? = null
)
