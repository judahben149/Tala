package com.judahben149.tala.presentation.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.judahben149.tala.data.local.getCurrentTimeMillis
import com.judahben149.tala.data.service.audio.SpeechPlayer
import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.domain.models.authentication.SignInMethod
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.session.PasswordUpdateData
import com.judahben149.tala.domain.usecases.authentication.DeleteAccountWithAuthUseCase
import com.judahben149.tala.domain.usecases.authentication.SignOutUseCase
import com.judahben149.tala.domain.usecases.settings.UpdateUserProfileUseCase
import com.judahben149.tala.domain.usecases.speech.DownloadTextToSpeechUseCase
import com.judahben149.tala.domain.usecases.speech.GetAllVoicesUseCase
import com.judahben149.tala.domain.usecases.speech.ObserveSelectedVoiceUseCase
import com.judahben149.tala.domain.usecases.speech.SaveSelectedVoiceUseCase
import com.judahben149.tala.domain.usecases.user.ClearPersistedUserUseCase
import com.judahben149.tala.domain.usecases.user.ObservePersistedUserDataUseCase
import com.judahben149.tala.util.decodeBase64Audio
import com.judahben149.tala.util.mimeTypeForOutputFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsScreenViewModel(
    private val observePersistedUserDataUseCase: ObservePersistedUserDataUseCase,
    private val updateUserProfileUseCase: UpdateUserProfileUseCase,
    private val deleteAccountWithAuthUseCase: DeleteAccountWithAuthUseCase,
    private val clearPersistedUserUseCase: ClearPersistedUserUseCase,
    private val observeSelectedVoiceUseCase: ObserveSelectedVoiceUseCase,
    private val saveSelectedVoiceUseCase: SaveSelectedVoiceUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val sessionManager: SessionManager,
    private val getAllVoicesUseCase: GetAllVoicesUseCase,
    private val downloadTextToSpeechUseCase: DownloadTextToSpeechUseCase,
    private val player: SpeechPlayer,
    private val logger: Logger
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        observeUserData()
        observeSelectedVoice()
        loadAvailableVoices()
    }

    private fun observeUserData() {
        viewModelScope.launch {
            observePersistedUserDataUseCase()
                .filterNotNull()
                .catch { exception ->
                    logger.e(exception) { "Error observing user data" }
                }
                .collect { appUser ->
                    logger.d { "User data just now: $appUser" }
                    _uiState.update {
                        it.copy(
                            user = appUser,
                            isLoading = false
                        )
                    }
                }
        }
    }

    private fun observeSelectedVoice() {
        viewModelScope.launch {
            observeSelectedVoiceUseCase()
                .catch { exception ->
                    logger.e(exception) { "Error observing selected voice" }
                }
                .collect { selectedVoice ->
                    _uiState.update {
                        it.copy(selectedVoice = selectedVoice)
                    }.also {
                        logger.d { "Selected voice just now: ${selectedVoice?.name}" }
                    }
                }
        }
    }

    private val samplePhrases = listOf(
        "Hello! I'm Tala, your AI language tutor.",
        "Let's practice speaking together and improve your skills.",
        "I'm here to help you become fluent in your target language.",
        "Ready to start your language learning journey?",
        "Great choice! Let's begin practicing conversations.",
        "I'll help you speak with confidence and clarity.",
        "Your pronunciation practice starts here with me.",
        "Welcome to Tala! I'm excited to be your speaking partner."
    )

    // Add this function to load voices
    private fun loadAvailableVoices() {
        viewModelScope.launch {
            when (val result = getAllVoicesUseCase()) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(availableVoices = result.data)
                    }
                }
                is Result.Failure -> {
                    logger.e { "Failed to load voices: ${result.error}" }
                }
            }
        }
    }


    fun playVoiceSample(voiceId: String) {
        viewModelScope.launch {
            try {
                _uiState.update {
                    it.copy(
                        isPlayingSample = true,
                        playingVoiceId = voiceId
                    )
                }

                val phrase = samplePhrases.random()

                when (val result = downloadTextToSpeechUseCase(phrase, voiceId)) {
                    is Result.Success -> {
                        val audioBytes = withContext(Dispatchers.IO) {
                            decodeBase64Audio(result.data.audioBase64 ?: "")
                        }
                        val mimeType = mimeTypeForOutputFormat("mp3_44100_128")

                        withContext(Dispatchers.Main) {
                            player.load(audioBytes, mimeType)
                            player.play()
                        }

                        logger.d { "Playing sample for voice: $voiceId" }
                    }
                    is Result.Failure -> {
                        logger.e { "Failed to generate sample audio: ${result.error}" }
                        _uiState.update {
                            it.copy(error = "Failed to play voice sample")
                        }
                    }
                }
            } catch (e: Exception) {
                logger.e(e) { "Error playing voice sample for $voiceId" }
                _uiState.update {
                    it.copy(error = "Failed to play voice sample")
                }
            } finally {
                _uiState.update {
                    it.copy(
                        isPlayingSample = false,
                        playingVoiceId = null
                    )
                }
            }
        }
    }

    fun showUpdateNameModal() {
        _uiState.update { it.copy(showUpdateNameModal = true) }
    }

    fun hideUpdateNameModal() {
        _uiState.update { it.copy(showUpdateNameModal = false) }
    }

    fun updateUserName(newName: String) {
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
                displayName = newName,
                firstName = newName.split(" ").firstOrNull() ?: newName,
                lastName = newName.split(" ").drop(1).joinToString(" "),
                updatedAt = getCurrentTimeMillis()
            )

            when (val result = updateUserProfileUseCase(updatedUser)) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(
                            isUpdatingProfile = false,
                            showUpdateNameModal = false
                        )
                    }
                    logger.d { "Profile name updated successfully" }
                }
                is Result.Failure -> {
                    _uiState.update {
                        it.copy(
                            error = "Failed to update name: ${result.error.message}",
                            isUpdatingProfile = false
                        )
                    }
                    logger.e { "Failed to update profile name: ${result.error}" }
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
            // Save selected voice in repository first
            saveSelectedVoiceUseCase(voiceId)
            sessionManager.saveVoiceSelectionCompleted()

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

    fun deleteAccount(password: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeletingAccount = true, error = null) }

            val currentUser = _uiState.value.user
            if (currentUser == null) {
                _uiState.update {
                    it.copy(
                        error = "No user data available",
                        isDeletingAccount = false
                    )
                }
                return@launch
            }

            // Check if password is required based on sign-in method
            val isPasswordRequired = currentUser.signInMethod == SignInMethod.EMAIL_PASSWORD

            if (isPasswordRequired && password.isNullOrBlank()) {
                _uiState.update {
                    it.copy(
                        error = "Password is required to delete your account",
                        isDeletingAccount = false
                    )
                }
                return@launch
            }

            // Use the password for email/password users, empty string for federated users
            val authPassword = if (isPasswordRequired) password!! else ""

            when (val result = deleteAccountWithAuthUseCase(authPassword)) {
                is Result.Success -> {
                    _uiState.update { it.copy(isDeletingAccount = false) }
                    logger.d { "Account deleted successfully" }
                    clearPersistedUserUseCase()
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

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            try {
                withContext(Dispatchers.Main) {
                    player.stop()
                }
            } catch (e: Exception) {
                logger.e(e) { "Error stopping player in onCleared" }
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