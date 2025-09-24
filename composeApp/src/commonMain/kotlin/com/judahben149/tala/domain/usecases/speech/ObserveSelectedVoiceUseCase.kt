package com.judahben149.tala.domain.usecases.speech

import com.judahben149.tala.domain.models.speech.SimpleVoice
import com.judahben149.tala.domain.repository.VoicesRepository
import kotlinx.coroutines.flow.Flow

class ObserveSelectedVoiceUseCase(
    private val repository: VoicesRepository
) {
    operator fun invoke(): Flow<SimpleVoice?> = repository.getSelectedVoiceFlow()
}