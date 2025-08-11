package com.judahben149.tala.domain.usecases.authentication

import com.judahben149.tala.domain.models.authentication.errors.FirebaseError
import com.judahben149.tala.domain.repository.AuthenticationRepository
import com.judahben149.tala.domain.models.common.Result

class SignOutUseCase(
    private val authRepository: AuthenticationRepository
) {
    suspend operator fun invoke(): Result<Unit, FirebaseError> {
        return authRepository.signOut()
    }
}
