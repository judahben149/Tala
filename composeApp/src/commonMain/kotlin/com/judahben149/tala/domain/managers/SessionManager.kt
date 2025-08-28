package com.judahben149.tala.domain.managers

import com.judahben149.tala.data.service.SignInStateTracker
import com.judahben149.tala.domain.models.user.Language
import com.judahben149.tala.util.IS_VOICE_SELECTION_COMPLETED
import com.judahben149.tala.util.SELECTED_INTERESTS_KEY
import com.judahben149.tala.util.SELECTED_VOICE_ID
import com.judahben149.tala.util.USER_LANGUAGE_LEARNING_CHOICE
import com.judahben149.tala.util.preferences.PrefsPersister

class SessionManager(
    private val persister: PrefsPersister,
    private val signInStateTracker: SignInStateTracker
) {

    fun saveUserLanguagePreference(language: Language) {
        persister.saveString(USER_LANGUAGE_LEARNING_CHOICE, language.name)
    }

    fun getUserLanguagePreference(): Language {
        val languageName = persister.fetchString(USER_LANGUAGE_LEARNING_CHOICE, Language.ENGLISH.name)
        return Language.valueOf(languageName)
    }

    fun getUserId(): String = signInStateTracker.getUserId()

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

    fun clearAllUserPreferences() {
        persister.removeKey(USER_LANGUAGE_LEARNING_CHOICE)
        persister.removeKey(SELECTED_INTERESTS_KEY)
        persister.removeKey(SELECTED_VOICE_ID)
        persister.removeKey(IS_VOICE_SELECTION_COMPLETED)
    }
}