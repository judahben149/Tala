package com.judahben149.tala.domain.usecases.authentication

import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.repository.UserRepository

class GetUserDataUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(userId: String): Result<Map<String, Any>, Exception> {
        return repository.getUserData(userId)
    }
}