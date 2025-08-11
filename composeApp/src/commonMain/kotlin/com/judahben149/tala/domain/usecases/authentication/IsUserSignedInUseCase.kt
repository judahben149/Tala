package com.judahben149.tala.domain.usecases.authentication

import com.judahben149.tala.domain.repository.AuthenticationRepository
import com.judahben149.tala.domain.models.common.Result

class IsUserSignedInUseCase(
    private val authRepository: AuthenticationRepository
) {
    operator fun invoke(): Boolean {
        return when (val result = authRepository.getCurrentUser()) {
            is Result.Success -> result.data != null
            else -> false
        }
    }
}
