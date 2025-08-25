package com.judahben149.tala.domain.usecases.settings

import com.judahben149.tala.domain.repository.UserRepository
import com.judahben149.tala.domain.models.common.Result

class DeleteAccountUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<Unit, Exception> {
        // Implementation will delete account
        TODO("Implementation needed")
    }
}