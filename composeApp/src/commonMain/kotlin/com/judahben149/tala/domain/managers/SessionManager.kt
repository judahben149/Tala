package com.judahben149.tala.domain.managers

import com.judahben149.tala.data.service.SignInStateTracker
import com.judahben149.tala.domain.models.language.Language
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
        val languageName = persister.loadString(USER_LANGUAGE_LEARNING_CHOICE, Language.ENGLISH.name)
        return Language.valueOf(languageName)
    }

    fun getUserId(): String = signInStateTracker.getUserId()
}