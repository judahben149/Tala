package com.judahben149.tala.data.service

import co.touchlab.kermit.Logger
import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class SignInStateTracker(
    private val settings: Settings = Settings()
): KoinComponent {
    private val _isSignedIn = MutableStateFlow<Boolean?>(null)
    val isSignedIn: StateFlow<Boolean?> = _isSignedIn.asStateFlow()

    private val _onboardingCompleted = MutableStateFlow<Boolean?>(null)
    val onboardingCompleted: StateFlow<Boolean?> = _onboardingCompleted.asStateFlow()

    private val logger: Logger = get()

    companion object {
        private const val KEY_IS_SIGNED_IN = "is_signed_in"
        private const val KEY_HAS_SIGNED_IN_BEFORE = "has_signed_in_before"
        private const val KEY_USER_ID = "current_user_id"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_SELECTED_LANGUAGES = "selected_languages"
        private const val KEY_SELECTED_INTERESTS = "selected_interests"
    }

    fun checkSignInState() {
        val isCurrentlySignedIn = settings.getBoolean(KEY_IS_SIGNED_IN, false)
        val hasCompletedOnboarding = settings.getBoolean(KEY_ONBOARDING_COMPLETED, false)

        logger.d { "checkSignInState: isCurrentlySignedIn=$isCurrentlySignedIn, hasCompletedOnboarding=$hasCompletedOnboarding" }

        _isSignedIn.value = isCurrentlySignedIn
        _onboardingCompleted.value = hasCompletedOnboarding
    }

    fun markSignedIn(userId: String, isNewUser: Boolean = false) {
        settings[KEY_IS_SIGNED_IN] = true
        settings[KEY_HAS_SIGNED_IN_BEFORE] = true
        settings[KEY_USER_ID] = userId

        logger.d { "markSignedIn: userId=$userId, isNewUser=$isNewUser" }

        // For existing users, assume onboarding is complete
        if (!isNewUser) {
            settings[KEY_ONBOARDING_COMPLETED] = true
            _onboardingCompleted.value = true
        }

        _isSignedIn.value = true
    }

    fun getUserId(): String = settings.getString(KEY_USER_ID, "")

    fun markOnboardingCompleted() {
        settings[KEY_ONBOARDING_COMPLETED] = true
        _onboardingCompleted.value = true
    }

    fun markSignedOut() {
        settings.remove(KEY_IS_SIGNED_IN)
        settings.remove(KEY_USER_ID)
        settings.remove(KEY_ONBOARDING_COMPLETED)
        _isSignedIn.value = false
        _onboardingCompleted.value = false
    }

    fun hasSignedInBefore(): Boolean {
        return settings.getBoolean(KEY_HAS_SIGNED_IN_BEFORE, false)
    }

    fun hasCompletedOnboarding(): Boolean {
        return settings.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }
}