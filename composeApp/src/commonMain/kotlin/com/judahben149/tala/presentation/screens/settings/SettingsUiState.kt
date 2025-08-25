package com.judahben149.tala.presentation.screens.settings

import com.judahben149.tala.domain.models.language.Language
import com.judahben149.tala.domain.models.session.UserProfile
import com.judahben149.tala.domain.models.speech.SimpleVoice

data class SettingsUiState(
    val user: UserProfile? = null,
    val selectedVoice: SimpleVoice? = null,
    val selectedLanguage: String = Language.ENGLISH.name,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isUpdatingProfile: Boolean = false,
    val isDeletingAccount: Boolean = false,
    val showDeleteConfirmation: Boolean = false,
    val showPasswordDialog: Boolean = false,
    val showVoiceSelector: Boolean = false,
    val showLanguageSelector: Boolean = false
)