package com.judahben149.tala.domain.usecases.authentication

import com.judahben149.tala.data.service.firebase.AppUser
import com.judahben149.tala.domain.models.authentication.errors.FirebaseError
import com.judahben149.tala.domain.models.authentication.errors.InvalidCredentialsError
import com.judahben149.tala.domain.models.authentication.errors.InvalidEmailError
import com.judahben149.tala.domain.repository.AuthenticationRepository
import com.judahben149.tala.domain.models.common.Result

class SignInUseCase(
    private val authRepository: AuthenticationRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AppUser, FirebaseError> {
        if (email.isBlank()) {
            return Result.Failure(InvalidEmailError("Email cannot be empty"))
        }
        if (password.isBlank()) {
            return Result.Failure(InvalidCredentialsError("Password cannot be empty"))
        }
        
        return authRepository.signIn(email, password)
    }
}
