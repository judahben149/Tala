package com.judahben149.tala.domain.usecases.speech

import com.judahben149.tala.domain.managers.SessionManager

class SetSelectedVoiceUseCase(
    private val sessionManager: SessionManager
) {
    operator fun invoke(voiceId: String) {
        sessionManager.saveSelectedVoice(voiceId)
    }
}