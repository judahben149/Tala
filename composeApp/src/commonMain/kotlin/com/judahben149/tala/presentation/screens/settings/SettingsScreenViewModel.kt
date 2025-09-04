package com.judahben149.tala.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.judahben149.tala.data.local.getCurrentTimeMillis
import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.session.PasswordUpdateData
import com.judahben149.tala.domain.usecases.authentication.DeleteAccountWithAuthUseCase
import com.judahben149.tala.domain.usecases.authentication.SignOutUseCase
import com.judahben149.tala.domain.usecases.settings.UpdateUserProfileUseCase
import com.judahben149.tala.domain.usecases.user.ClearPersistedUserUseCase
import com.judahben149.tala.domain.usecases.user.ObservePersistedUserDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingsScreenViewModel(
    private val observePersistedUserDataUseCase: ObservePersistedUserDataUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val deleteAccountWithAuthUseCase: DeleteAccountWithAuthUseCase,
    private val clearPersistedUserUseCase: ClearPersistedUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val sessionManager: SessionManager,
    private val logger: Logger
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeUserData()
    }

    private fun observeUserData() {
        viewModelScope.launch {
            observePersistedUserDataUseCase()
                .filterNotNull()
                .catch { exception ->
                    logger.e(exception) { "Error observing user data" }
                }
                .collect { appUser ->
                    _uiState.update {
                        it.copy(
                            user = appUser,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun updateProfile(name: String, email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isUpdatingProfile = true, error = null) }

            val currentUser = _uiState.value.user
            if (currentUser == null) {
                _uiState.update {
                    it.copy(
                        error = "No user data available",
                        isUpdatingProfile = false
                    )
                }
                return@launch
            }

            val updatedUser = currentUser.copy(
                displayName = name,
                email = email,
                firstName = name.split(" ").firstOrNull() ?: name,
                lastName = name.split(" ").drop(1).joinToString(" "),
                updatedAt = getCurrentTimeMillis()
            )

            when (val result = updateUserProfileUseCase(updatedUser)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(isUpdatingProfile = false)
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

    fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            val currentUser = _uiState.value.user
            if (currentUser == null) return@launch

            val updatedUser = currentUser.copy(
                notificationsEnabled = enabled,
                updatedAt = getCurrentTimeMillis()
            )

            when (val result = updateUserProfileUseCase(updatedUser)) {
                is Result.Success -> {
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
            val currentUser = _uiState.value.user
            if (currentUser == null) return@launch

            val updatedUser = currentUser.copy(
                practiceRemindersEnabled = enabled,
                updatedAt = getCurrentTimeMillis()
            )

            when (val result = updateUserProfileUseCase(updatedUser)) {
                is Result.Success -> {
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

    fun selectLanguage(language: String) {
        viewModelScope.launch {
            val currentUser = _uiState.value.user
            if (currentUser == null) return@launch

            val updatedUser = currentUser.copy(
                learningLanguage = language,
                updatedAt = getCurrentTimeMillis()
            )

            when (val result = updateUserProfileUseCase(updatedUser)) {
                is Result.Success -> {
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

    fun selectVoice(voiceId: String) {
        viewModelScope.launch {
            val currentUser = _uiState.value.user
            if (currentUser == null) return@launch

            val updatedUser = currentUser.copy(
                selectedVoiceId = voiceId,
                updatedAt = getCurrentTimeMillis()
            )

            when (val result = updateUserProfileUseCase(updatedUser)) {
                is Result.Success -> {
                    logger.d { "Voice updated to: $voiceId" }
                }
                is Result.Failure -> {
                    _uiState.update {
                        it.copy(error = "Failed to update voice: ${result.error.message}")
                    }
                    logger.e { "Failed to update selected voice: ${result.error}" }
                }
            }
        }
    }

    fun updatePassword(passwordData: PasswordUpdateData) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Password update doesn't affect user profile data, so no local update needed
            // Just handle Firebase authentication update
            // Implementation depends on your password update use case

            _uiState.update { it.copy(isLoading = false) }
            logger.d { "Password update functionality to be implemented" }
        }
    }

    fun deleteAccount(password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAccount = true, error = null) }

            when (val result = deleteAccountWithAuthUseCase(password)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isDeletingAccount = false) }
                    logger.d { "Account deleted successfully" }
                    sessionManager.markSignedOut()
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
                    sessionManager.markSignedOut()
                    clearPersistedUserUseCase()
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

    // Dialog state management methods remain the same
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