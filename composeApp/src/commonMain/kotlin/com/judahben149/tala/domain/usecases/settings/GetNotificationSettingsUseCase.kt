package com.judahben149.tala.domain.usecases.settings

import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.session.NotificationSettings
import com.judahben149.tala.domain.repository.UserRepository

class GetNotificationSettingsUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<NotificationSettings, Exception> {
        return userRepository.getNotificationSettings()
    }
}