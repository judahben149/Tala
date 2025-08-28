package com.judahben149.tala.domain.usecases.authentication

import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthException
import com.judahben149.tala.domain.repository.AuthenticationRepository
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.user.AppUser

class SignInUseCase(
    private val authRepository: AuthenticationRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AppUser, FirebaseAuthException> {
        return authRepository.signIn(email, password)
    }
}
