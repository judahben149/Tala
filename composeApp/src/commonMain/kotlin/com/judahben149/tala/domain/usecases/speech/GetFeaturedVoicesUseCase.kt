package com.judahben149.tala.domain.usecases.speech

import com.judahben149.tala.BuildKonfig
import com.judahben149.tala.domain.models.authentication.errors.NetworkException
import com.judahben149.tala.domain.models.speech.SimpleVoice
import com.judahben149.tala.domain.repository.VoicesRepository
import com.judahben149.tala.domain.models.common.Result

class GetFeaturedVoicesUseCase(
    private val repository: VoicesRepository
) {
    suspend operator fun invoke(): Result<List<SimpleVoice>, NetworkException> {
        return repository.getFeaturedVoices(BuildKonfig.ELEVEN_LABS_API_KEY)
    }

}