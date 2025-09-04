package com.judahben149.tala.presentation.screens.settings

import com.judahben149.tala.domain.models.user.Language
import com.judahben149.tala.domain.models.session.UserProfile
import com.judahben149.tala.domain.models.speech.SimpleVoice
import com.judahben149.tala.domain.models.user.AppUser

data class SettingsUiState(
    val user: AppUser? = null,
    val isLoading: Boolean = false,
    val selectedVoice: SimpleVoice? = null,
    val error: String? = null,
    val isUpdatingProfile: Boolean = false,
    val isDeletingAccount: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val showPasswordDialog: Boolean = false,
    val showVoiceSelector: Boolean = false,
    val showLanguageSelector: Boolean = false,
    val availableVoices: List<SimpleVoice> = emptyList(),
    val isPlayingSample: Boolean = false,
    val playingVoiceId: String? = null,
    val showUpdateNameModal: Boolean = false
)