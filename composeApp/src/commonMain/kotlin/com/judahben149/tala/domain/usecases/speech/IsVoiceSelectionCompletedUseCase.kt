package com.judahben149.tala.domain.usecases.speech

import com.judahben149.tala.domain.managers.SessionManager

class IsVoiceSelectionCompletedUseCase(
    private val sessionManager: SessionManager
) {
    operator fun invoke(): Boolean {
        return sessionManager.isVoiceSelectionCompleted()
    }
}