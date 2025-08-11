package com.judahben149.tala.domain.usecases.authentication

import com.judahben149.tala.data.service.firebase.AppUser
import com.judahben149.tala.domain.models.authentication.errors.FirebaseError
import com.judahben149.tala.domain.models.authentication.errors.InvalidEmailError
import com.judahben149.tala.domain.models.authentication.errors.UnknownFirebaseError
import com.judahben149.tala.domain.models.authentication.errors.WeakPasswordError
import com.judahben149.tala.domain.repository.AuthenticationRepository
import com.judahben149.tala.domain.models.common.Result

class CreateUserUseCase(
    private val authRepository: AuthenticationRepository
) {
    suspend operator fun invoke(
        email: String, 
        password: String, 
        displayName: String
    ): Result<AppUser, FirebaseError> {
        if (email.isBlank()) {
            return Result.Failure(InvalidEmailError("Email cannot be empty"))
        }
        if (password.isBlank()) {
            return Result.Failure(WeakPasswordError("Password cannot be empty"))
        }
        if (displayName.isBlank()) {
            return Result.Failure(UnknownFirebaseError("Display name cannot be empty"))
        }
        
        return authRepository.createUser(email, password, displayName)
    }
}
