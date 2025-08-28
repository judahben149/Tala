package com.judahben149.tala.domain.usecases.speech

import com.judahben149.tala.domain.managers.SessionManager

class GetSelectedVoiceIdUseCase(
    private val sessionManager: SessionManager
) {
    operator fun invoke(): String {
        val voiceId = sessionManager.getSelectedVoiceId()
        return voiceId
    }
}