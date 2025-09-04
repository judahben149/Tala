package com.judahben149.tala.domain.usecases.speech

import com.judahben149.tala.domain.repository.VoicesRepository

class SaveSelectedVoiceUseCase(
    private val repository: VoicesRepository
) {
    suspend operator fun invoke(voiceId: String) = repository.saveSelectedVoice(voiceId)
}