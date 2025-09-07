package com.judahben149.tala.domain.usecases.speech

import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.domain.repository.UserRepository

class GetSelectedVoiceIdUseCase(
    private val sessionManager: SessionManager,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): String {
        return when(val result = userRepository.getPersistedUser()) {
            null -> sessionManager.getSelectedVoiceId()
            else -> result.selectedVoiceId ?: sessionManager.getSelectedVoiceId()
        }
    }
}