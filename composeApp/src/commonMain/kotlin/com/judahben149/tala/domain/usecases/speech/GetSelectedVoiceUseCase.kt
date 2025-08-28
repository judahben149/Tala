package com.judahben149.tala.domain.usecases.speech

import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.speech.SimpleVoice
import com.judahben149.tala.domain.repository.VoicesRepository

class GetSelectedVoiceUseCase(
    private val sessionManager: SessionManager,
    private val voicesRepository: VoicesRepository
) {
    suspend operator fun invoke(): Result<SimpleVoice, Exception> {
        val voiceId = sessionManager.getSelectedVoiceId()

        return try {
            val voice = voicesRepository.getVoiceById(voiceId)
            if (voice != null) {
                Result.Success(voice)
            } else {
                Result.Failure(Exception("Voice not found"))
            }
        }catch (e: Exception) {
            Result.Failure(e)
        }
    }
}