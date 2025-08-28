package com.judahben149.tala.domain.usecases.settings

import com.judahben149.tala.domain.repository.UserRepository
import com.judahben149.tala.domain.models.common.Result

class UpdateNotificationSettingsUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        notificationsEnabled: Boolean,
        practiceRemindersEnabled: Boolean
    ): Result<Unit, Exception> {
        return repository.updateNotificationSettings(
            notificationsEnabled,
            practiceRemindersEnabled
        )
    }
}