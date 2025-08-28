package com.judahben149.tala.domain.managers

import co.touchlab.kermit.Logger
import com.judahben149.tala.domain.models.user.Language
import com.judahben149.tala.util.preferences.PrefsPersister
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SessionManager(
    private val persister: PrefsPersister,
    private val logger: Logger
) {

    private val _appState = MutableStateFlow<AppState>(AppState.Unknown)
    val appState: StateFlow<AppState> = _appState.asStateFlow()

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_HAS_SIGNED_IN_BEFORE = "has_signed_in_before"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val USER_LANGUAGE_LEARNING_CHOICE = "user_language_learning_choice"
        private const val SELECTED_INTERESTS_KEY = "selected_interests"
        private const val IS_VOICE_SELECTION_COMPLETED = "IS_VOICE_SELECTION_COMPLETED"
        private const val SELECTED_VOICE_ID = "SELECTED_VOICE_ID"
    }


    fun checkAppState() {
        val userId = getUserId()
        val hasSignedInBefore = persister.fetchBoolean(KEY_HAS_SIGNED_IN_BEFORE, false)
        val onboardingCompleted = persister.fetchBoolean(KEY_ONBOARDING_COMPLETED, false)

        logger.d { "checkAppState: userId=$userId, hasSignedInBefore=$hasSignedInBefore, onboardingCompleted=$onboardingCompleted" }

        val newState = when {
            userId.isEmpty() -> AppState.LoggedOut
            !onboardingCompleted -> AppState.NeedsOnboarding
            hasSignedInBefore -> AppState.LoggedIn
            else -> AppState.LoggedOut
        }

        _appState.value = newState
        logger.d { "App state updated to: $newState" }
    }

    fun markSignedIn(userId: String, isNewUser: Boolean = false) {
        persister.saveString(KEY_USER_ID, userId)
        persister.saveBoolean(KEY_HAS_SIGNED_IN_BEFORE, true)

        logger.d { "markSignedIn: userId=$userId, isNewUser=$isNewUser" }

        // Check actual onboarding completion for existing users
        if (!isNewUser) {
            // Check if this existing user previously completed onboarding
            val hadCompletedOnboarding = persister.fetchBoolean(KEY_ONBOARDING_COMPLETED, false)
            if (hadCompletedOnboarding) {
                // User completed onboarding before, so they're fully set up
                _appState.value = AppState.LoggedIn
            } else {
                // User exists but never completed onboarding - they need to finish it
                persister.saveBoolean(KEY_ONBOARDING_COMPLETED, false)
                _appState.value = AppState.NeedsOnboarding
            }
        } else {
            // New user definitely needs onboarding
            persister.saveBoolean(KEY_ONBOARDING_COMPLETED, false)
            _appState.value = AppState.NeedsOnboarding
        }
    }

    fun markOnboardingCompleted() {
        persister.saveBoolean(KEY_ONBOARDING_COMPLETED, true)
        _appState.value = AppState.LoggedIn
        logger.d { "Onboarding completed - transitioning to LoggedIn state" }
    }

    fun markSignedOut() {
        persister.removeKey(KEY_USER_ID)
        persister.removeKey(KEY_ONBOARDING_COMPLETED)
        // Keep hasSignedInBefore for UX purposes

        _appState.value = AppState.LoggedOut
        logger.d { "User signed out" }
    }

    fun saveUserLanguagePreference(language: Language) {
        persister.saveString(USER_LANGUAGE_LEARNING_CHOICE, language.name)
    }

    fun getUserLanguagePreference(): Language {
        val languageName = persister.fetchString(USER_LANGUAGE_LEARNING_CHOICE, Language.ENGLISH.name)
        return Language.valueOf(languageName)
    }

    fun getUserId(): String = persister.fetchString(KEY_USER_ID, "")

    fun saveSelectedVoice(voiceId: String) {
        persister.saveString(SELECTED_VOICE_ID, voiceId)
    }

    fun getSelectedVoiceId(defaultVoiceId: String = "21m00Tcm4TlvDq8ikWAM"): String {
        return persister.fetchString(SELECTED_VOICE_ID, defaultVoiceId)
    }

    fun isVoiceSelectionCompleted(): Boolean {
        return persister.fetchBoolean(IS_VOICE_SELECTION_COMPLETED, false)
    }

    fun saveVoiceSelectionCompleted() {
        persister.saveBoolean(IS_VOICE_SELECTION_COMPLETED, true)
    }

    fun saveUserInterests(interests: List<String>) {
        persister.saveStringSet(SELECTED_INTERESTS_KEY, interests.toSet())
    }

    fun getUserInterests(): Set<String> {
        return persister.fetchStringSet(SELECTED_INTERESTS_KEY, emptySet())
    }

    fun clearUserInterests() {
        persister.removeKey(SELECTED_INTERESTS_KEY)
    }

    fun hasSignedInBefore(): Boolean {
        return persister.fetchBoolean(KEY_HAS_SIGNED_IN_BEFORE, false)
    }

    fun hasCompletedOnboarding(): Boolean {
        return persister.fetchBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun clearAllUserPreferences() {
        persister.removeKey(USER_LANGUAGE_LEARNING_CHOICE)
        persister.removeKey(SELECTED_INTERESTS_KEY)
        persister.removeKey(SELECTED_VOICE_ID)
        persister.removeKey(IS_VOICE_SELECTION_COMPLETED)
    }

    suspend fun checkUserOnboardingStatus(userId: String): Boolean {
        // Check if user has required onboarding data
        val hasLanguage = getUserLanguagePreference() != Language.ENGLISH ||
                persister.fetchString(USER_LANGUAGE_LEARNING_CHOICE, "").isNotEmpty()
        val hasInterests = getUserInterests().isNotEmpty()

        return hasLanguage && hasInterests
    }


    enum class AppState {
        Unknown,
        LoggedOut,
        NeedsOnboarding,
        LoggedIn
    }
}