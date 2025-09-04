package com.judahben149.tala.domain.usecases.user

import com.judahben149.tala.domain.repository.UserRepository
import com.judahben149.tala.domain.models.common.Result

class ClearPersistedUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<Unit, Exception> {
        return userRepository.clearPersistedUser()
    }
}