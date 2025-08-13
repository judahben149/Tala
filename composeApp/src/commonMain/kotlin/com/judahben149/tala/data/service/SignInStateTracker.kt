package com.judahben149.tala.data.service

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SignInStateTracker(
    private val settings: Settings = Settings()
) {
    private val _isSignedIn = MutableStateFlow<Boolean?>(null)
    val isSignedIn: StateFlow<Boolean?> = _isSignedIn.asStateFlow()

    companion object {
        private const val KEY_IS_SIGNED_IN = "is_signed_in"
        private const val KEY_HAS_SIGNED_IN_BEFORE = "has_signed_in_before"
        private const val KEY_USER_ID = "current_user_id"
    }

    /**
     * Check sign-in state when app opens (offline-first)
     */
    fun checkSignInState() {
        // Simply read from local storage, no Firebase calls
        val isCurrentlySignedIn = settings.getBoolean(KEY_IS_SIGNED_IN, false)
        _isSignedIn.value = isCurrentlySignedIn
    }

    /**
     * Mark user as signed in (call after successful sign in)
     */
    fun markSignedIn(userId: String) {
        settings[KEY_IS_SIGNED_IN] = true
        settings[KEY_HAS_SIGNED_IN_BEFORE] = true
        settings[KEY_USER_ID] = userId
        _isSignedIn.value = true
    }

    /**
     * Mark user as signed out (call after sign out)
     */
    fun markSignedOut() {
        settings.remove(KEY_IS_SIGNED_IN)
        settings.remove(KEY_USER_ID)
        // Keep the "has signed in before" flag
        _isSignedIn.value = false
    }

    /**
     * Check if user has ever signed in before (for showing login vs signup)
     */
    fun hasSignedInBefore(): Boolean {
        return settings.getBoolean(KEY_HAS_SIGNED_IN_BEFORE, false)
    }

    /**
     * Get stored user ID if currently signed in
     */
    fun getStoredUserId(): String? {
        return if (settings.getBoolean(KEY_IS_SIGNED_IN, false)) {
            settings.getString(KEY_USER_ID, "")
        } else null
    }

    /**
     * Clear all data (for debugging or reset purposes)
     */
    fun clearAllData() {
        settings.remove(KEY_IS_SIGNED_IN)
        settings.remove(KEY_HAS_SIGNED_IN_BEFORE)
        settings.remove(KEY_USER_ID)
        _isSignedIn.value = false
    }
}
