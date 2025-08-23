package com.judahben149.tala.domain.usecases.speech

import com.judahben149.tala.domain.managers.SessionManager

class SetVoiceSelectionCompleteUseCase(
    private val sessionManager: SessionManager
) {
    operator fun invoke() {
        return sessionManager.saveVoiceSelectionCompleted()
    }
}