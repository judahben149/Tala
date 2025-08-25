package com.judahben149.tala.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.session.PasswordUpdateData
import com.judahben149.tala.domain.models.speech.SimpleVoice
import com.judahben149.tala.domain.usecases.authentication.SignOutUseCase
import com.judahben149.tala.domain.usecases.settings.DeleteAccountUseCase
import com.judahben149.tala.domain.usecases.settings.GetLearningLanguageUseCase
import com.judahben149.tala.domain.usecases.settings.GetUserProfileUseCase
import com.judahben149.tala.domain.usecases.settings.UpdateLearningLanguageUseCase
import com.judahben149.tala.domain.usecases.settings.UpdateNotificationSettingsUseCase
import com.judahben149.tala.domain.usecases.settings.UpdatePasswordUseCase
import com.judahben149.tala.domain.usecases.settings.UpdateUserProfileUseCase
import com.judahben149.tala.domain.usecases.speech.GetAllVoicesUseCase
import com.judahben149.tala.domain.usecases.speech.GetSelectedVoiceUseCase
import com.judahben149.tala.domain.usecases.speech.SetSelectedVoiceUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsScreenViewModel(
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val updatePasswordUseCase: UpdatePasswordUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val getSelectedVoiceUseCase: GetSelectedVoiceUseCase,
    private val setSelectedVoiceUseCase: SetSelectedVoiceUseCase,
    private val getAllVoicesUseCase: GetAllVoicesUseCase,
    private val updateLearningLanguageUseCase: UpdateLearningLanguageUseCase,
    private val getLearningLanguageUseCase: GetLearningLanguageUseCase,
    private val updateNotificationSettingsUseCase: UpdateNotificationSettingsUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val logger: Logger
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    private val availableLanguages = listOf(
        "Spanish", "French", "German", "Italian", "Portuguese", 
        "Japanese", "Korean", "Chinese", "Russian", "Arabic"
    )

    init {
        loadUserProfile()
        loadSelectedVoice()
        loadLearningLanguage()
    }

    private fun loadUserProfile() {
//        viewModelScope.launch {
//            _uiState.update { it.copy(isLoading = true) }
//
//            when (val result = getUserProfileUseCase()) {
//                is Result.Success -> {
//                    _uiState.update {
//                        it.copy(
//                            user = result.data,
//                            isLoading = false
//                        )
//                    }
//                }
//                is Result.Failure -> {
//                    _uiState.update {
//                        it.copy(
//                            error = "Failed to load profile: ${result.error.message}",
//                            isLoading = false
//                        )
//                    }
//                    logger.e { "Failed to load user profile: ${result.error}" }
//                }
//            }
//        }
    }

    private fun loadSelectedVoice() {
        viewModelScope.launch {
            // Implementation will fetch selected voice
        }
    }

    private fun loadLearningLanguage() {
        viewModelScope.launch {
            // Implementation will fetch learning language
        }
    }

    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingProfile = true) }
            
            // Implementation will update profile
            
            _uiState.update { it.copy(isUpdatingProfile = false) }
        }
    }

    fun updatePassword(passwordData: PasswordUpdateData) {
        viewModelScope.launch {
            // Implementation will update password
        }
    }

    fun selectVoice(voice: SimpleVoice) {
        viewModelScope.launch {
            // Implementation will update selected voice
        }
    }

    fun selectLanguage(language: String) {
        viewModelScope.launch {
            // Implementation will update learning language
        }
    }

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            // Implementation will update notification settings
        }
    }

    fun togglePracticeReminders(enabled: Boolean) {
        viewModelScope.launch {
            // Implementation will update practice reminder settings
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAccount = true) }
            
            // Implementation will delete account
            
            _uiState.update { it.copy(isDeletingAccount = false) }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            // Implementation will sign out user
        }
    }

    fun showDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmation = true) }
    }

    fun hideDeleteConfirmation() {
        _uiState.update { it.copy(showDeleteConfirmation = false) }
    }

    fun showPasswordDialog() {
        _uiState.update { it.copy(showPasswordDialog = true) }
    }

    fun hidePasswordDialog() {
        _uiState.update { it.copy(showPasswordDialog = false) }
    }

    fun showVoiceSelector() {
        _uiState.update { it.copy(showVoiceSelector = true) }
    }

    fun hideVoiceSelector() {
        _uiState.update { it.copy(showVoiceSelector = false) }
    }

    fun showLanguageSelector() {
        _uiState.update { it.copy(showLanguageSelector = true) }
    }

    fun hideLanguageSelector() {
        _uiState.update { it.copy(showLanguageSelector = false) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
