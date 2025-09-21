package com.judahben149.tala.domain.usecases.speech

import com.judahben149.tala.BuildKonfig
import com.judahben149.tala.domain.managers.RemoteConfigManager
import com.judahben149.tala.domain.repository.VoicesRepository

class GetAllVoicesUseCase(
    private val repository: VoicesRepository,
    private val remoteConfigManager: RemoteConfigManager
) {

    suspend operator fun invoke() = repository.getAllVoices(remoteConfigManager.getString("eleven_labs_api_key" , BuildKonfig.ELEVEN_LABS_API_KEY))
}