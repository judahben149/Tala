package com.judahben149.tala.domain.usecases.authentication

import com.judahben149.tala.data.service.firebase.AppUser
import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthException
import com.judahben149.tala.domain.repository.AuthenticationRepository
import com.judahben149.tala.domain.models.common.Result

class CreateUserUseCase(
    private val authRepository: AuthenticationRepository
) {
    suspend operator fun invoke(
        email: String, 
        password: String, 
        displayName: String
    ): Result<AppUser, FirebaseAuthException> {
        return authRepository.createUser(email, password, displayName)
    }
}
