package com.judahben149.tala.domain.usecases.authentication

import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthException
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.repository.AuthenticationRepository

class SignOutUseCase(
    private val authRepository: AuthenticationRepository
) {
    suspend operator fun invoke(): Result<Unit, FirebaseAuthException> {
        return authRepository.signOut()
    }
}
