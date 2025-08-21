package com.judahben149.tala.domain.usecases.authentication

import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthException
import com.judahben149.tala.domain.repository.AuthenticationRepository
import com.judahben149.tala.domain.models.common.Result

class SendPasswordResetEmailUseCase(
    private val authRepository: AuthenticationRepository
) {
    suspend operator fun invoke(email: String): Result<Unit, FirebaseAuthException> {
        return authRepository.sendPasswordResetEmail(email)
    }
}