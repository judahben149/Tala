package com.judahben149.tala.domain.usecases.speech

import com.judahben149.tala.domain.managers.SessionManager

class GetSelectedVoiceUseCase(
    private val sessionManager: SessionManager
) {
    operator fun invoke(): String {
        return sessionManager.getSelectedVoiceId()
    }
}