package com.judahben149.tala.domain.usecases.settings

import com.judahben149.tala.domain.repository.UserRepository
import com.judahben149.tala.domain.models.common.Result

class UpdatePasswordUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        currentPassword: String,
        newPassword: String
    ): Result<Unit, Exception> {
        // Implementation will update password
        TODO("Implementation needed")
    }
}