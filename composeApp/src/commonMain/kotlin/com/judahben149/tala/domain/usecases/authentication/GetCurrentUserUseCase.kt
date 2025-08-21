package com.judahben149.tala.domain.usecases.authentication

import com.judahben149.tala.data.service.firebase.AppUser
import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthException
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.repository.AuthenticationRepository

class GetCurrentUserUseCase(
    private val authRepository: AuthenticationRepository
) {
    operator fun invoke(): Result<AppUser?, FirebaseAuthException> {
        return authRepository.getCurrentUser()
    }
}
