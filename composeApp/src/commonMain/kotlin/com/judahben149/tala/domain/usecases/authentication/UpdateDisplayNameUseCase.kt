package com.judahben149.tala.domain.usecases.authentication

import com.judahben149.tala.domain.models.authentication.errors.FirebaseError
import com.judahben149.tala.domain.models.authentication.errors.UnknownFirebaseError
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.repository.AuthenticationRepository

class UpdateDisplayNameUseCase(
    private val authRepository: AuthenticationRepository
) {
    suspend operator fun invoke(displayName: String): Result<Unit, FirebaseError> {
        if (displayName.isBlank()) {
            return Result.Failure(UnknownFirebaseError("Display name cannot be empty"))
        }
        
        return authRepository.setDisplayName(displayName)
    }
}
