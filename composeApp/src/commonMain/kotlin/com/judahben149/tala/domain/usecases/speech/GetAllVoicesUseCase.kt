package com.judahben149.tala.domain.usecases.speech

import com.judahben149.tala.BuildKonfig
import com.judahben149.tala.domain.repository.VoicesRepository

class GetAllVoicesUseCase(
    private val repository: VoicesRepository
) {

    suspend operator fun invoke() = repository.getAllVoices(BuildKonfig.ELEVEN_LABS_API_KEY)
}