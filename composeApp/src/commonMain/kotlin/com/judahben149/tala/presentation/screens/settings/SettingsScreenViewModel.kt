package com.judahben149.tala.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.judahben149.tala.data.service.SignInStateTracker
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.session.PasswordUpdateData
import com.judahben149.tala.domain.models.session.UserProfile
import com.judahben149.tala.domain.models.speech.SimpleVoice
import com.judahben149.tala.domain.usecases.authentication.DeleteAccountWithAuthUseCase
import com.judahben149.tala.domain.usecases.authentication.SignOutUseCase
import com.judahben149.tala.domain.usecases.settings.GetLearningLanguageUseCase
import com.judahben149.tala.domain.usecases.settings.GetNotificationSettingsUseCase
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
    private val deleteAccountWithAuthUseCase: DeleteAccountWithAuthUseCase,
    private val getSelectedVoiceUseCase: GetSelectedVoiceUseCase,
    private val setSelectedVoiceUseCase: SetSelectedVoiceUseCase,
    private val getAllVoicesUseCase: GetAllVoicesUseCase,
    private val updateLearningLanguageUseCase: UpdateLearningLanguageUseCase,
    private val getLearningLanguageUseCase: GetLearningLanguageUseCase,
    private val updateNotificationSettingsUseCase: UpdateNotificationSettingsUseCase,
    private val getNotificationSettingsUseCase: GetNotificationSettingsUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val signInStateTracker: SignInStateTracker,
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
        loadNotificationSettings()
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
            when (val result = getSelectedVoiceUseCase()) {
                is Result.Success -> {
                    _uiState.update { it.copy(selectedVoice = result.data) }
                }

                is Result.Failure -> {
                    logger.e { "Failed to load selected voice: ${result.error}" }
                    // Continue without error as this is optional
                }
            }
        }
    }


    private fun loadLearningLanguage() {
        viewModelScope.launch {
            when (val result = getLearningLanguageUseCase()) {
                is Result.Success -> {
                    _uiState.update { it.copy(selectedLanguage = result.data.name) }
                }
                is Result.Failure -> {
                    logger.e { "Failed to load learning language: ${result.error}" }
                    // Use default language
                    _uiState.update { it.copy(selectedLanguage = "Spanish") }
                }
            }
        }
    }

    private fun loadNotificationSettings() {
        viewModelScope.launch {
            when (val result = getNotificationSettingsUseCase()) {
                is Result.Success -> {
                    val settings = result.data

                    val updatedUser = _uiState.value.user?.copy(
                        notificationsEnabled = settings.notificationsEnabled,
                        practiceRemindersEnabled = settings.practiceRemindersEnabled
                    ) ?: UserProfile(
                        id = "",
                        name = "",
                        email = "",
                        avatarUrl = "",
                        createdAt = 0,
                        streakDays = 0,
                        totalConversations = 0,
                        notificationsEnabled = true,
                        practiceRemindersEnabled = true
                    )

                    _uiState.update { it.copy(user = updatedUser) }
                    logger.d { "Notification settings loaded: $settings" }
                }
                is Result.Failure -> {
                    logger.e { "Failed to load notification settings: ${result.error}" }
                    // Use defaults if loading fails
                }
            }
        }
    }


    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingProfile = true, error = null) }

            when (val result = updateUserProfileUseCase(name, email)) {
                is Result.Success -> {
                    // Update local user state
                    val updatedUser = _uiState.value.user?.copy(name = name, email = email)
                    _uiState.update {
                        it.copy(
                            user = updatedUser,
                            isUpdatingProfile = false
                        )
                    }
                    logger.d { "Profile updated successfully" }
                }
                is Result.Failure -> {
                    _uiState.update {
                        it.copy(
                            error = "Failed to update profile: ${result.error.message}",
                            isUpdatingProfile = false
                        )
                    }
                    logger.e { "Failed to update profile: ${result.error}" }
                }
            }
        }
    }


    fun updatePassword(passwordData: PasswordUpdateData) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = updatePasswordUseCase(passwordData.currentPassword, passwordData.newPassword)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    logger.d { "Password updated successfully" }
                }
                is Result.Failure -> {
                    _uiState.update {
                        it.copy(
                            error = "Failed to update password: ${result.error.message}",
                            isLoading = false
                        )
                    }
                    logger.e { "Failed to update password: ${result.error}" }
                }
            }
        }
    }


    fun selectVoice(voice: SimpleVoice) {
        viewModelScope.launch {
            // Implementation will update selected voice
        }
    }

    fun selectLanguage(language: String) {
        viewModelScope.launch {
            when (val result = updateLearningLanguageUseCase(language)) {
                is Result.Success -> {
                    _uiState.update { it.copy(selectedLanguage = language) }
                    logger.d { "Learning language updated to: $language" }
                }
                is Result.Failure -> {
                    _uiState.update {
                        it.copy(error = "Failed to update language: ${result.error.message}")
                    }
                    logger.e { "Failed to update learning language: ${result.error}" }
                }
            }
        }
    }


    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            val currentReminders = _uiState.value.user?.practiceRemindersEnabled ?: true

            when (val result = updateNotificationSettingsUseCase(enabled, currentReminders)) {
                is Result.Success -> {
                    val updatedUser = _uiState.value.user?.copy(notificationsEnabled = enabled)
                    _uiState.update { it.copy(user = updatedUser) }
                    logger.d { "Notifications ${if (enabled) "enabled" else "disabled"}" }
                }
                is Result.Failure -> {
                    _uiState.update {
                        it.copy(error = "Failed to update notifications: ${result.error.message}")
                    }
                    logger.e { "Failed to update notification settings: ${result.error}" }
                }
            }
        }
    }


    fun togglePracticeReminders(enabled: Boolean) {
        viewModelScope.launch {
            val currentNotifications = _uiState.value.user?.notificationsEnabled ?: true

            when (val result = updateNotificationSettingsUseCase(currentNotifications, enabled)) {
                is Result.Success -> {
                    val updatedUser = _uiState.value.user?.copy(practiceRemindersEnabled = enabled)
                    _uiState.update { it.copy(user = updatedUser) }
                    logger.d { "Practice reminders ${if (enabled) "enabled" else "disabled"}" }
                }
                is Result.Failure -> {
                    _uiState.update {
                        it.copy(error = "Failed to update reminders: ${result.error.message}")
                    }
                    logger.e { "Failed to update practice reminder settings: ${result.error}" }
                }
            }
        }
    }


    fun deleteAccount(password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAccount = true, error = null) }

            when (val result = deleteAccountWithAuthUseCase(password)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isDeletingAccount = false) }
                    logger.d { "Account deleted successfully" }

                    signInStateTracker.markSignedOut()
                    // Trigger navigation away from app
                }
                is Result.Failure -> {
                    _uiState.update {
                        it.copy(
                            error = when {
                                result.error.message?.contains("wrong-password") == true ->
                                    "Incorrect password. Please try again."
                                result.error.message?.contains("too-many-requests") == true ->
                                    "Too many attempts. Please try again later."
                                else -> "Failed to delete account: ${result.error.message}"
                            },
                            isDeletingAccount = false
                        )
                    }
                    logger.e { "Failed to delete account: ${result.error}" }
                }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = signOutUseCase()) {
                is Result.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    logger.d { "User signed out successfully" }
                    // Sign out successful - this should trigger navigation to login
                }
                is Result.Failure -> {
                    _uiState.update {
                        it.copy(
                            error = "Failed to sign out: ${result.error.message}",
                            isLoading = false
                        )
                    }
                    logger.e { "Failed to sign out: ${result.error}" }
                }
            }
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
