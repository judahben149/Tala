package com.judahben149.tala.domain.usecases.settings

import com.judahben149.tala.domain.repository.UserRepository
import com.judahben149.tala.domain.models.common.Result

class UpdateUserProfileUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(name: String, email: String): Result<Unit, Exception> {
        // Implementation will update user profile
        return repository.updateUserProfile(name, email)
    }

    suspend fun saveUserProfile(userId: String, profileData: Map<String, Any>): Result<Unit, Exception> {
        return repository.saveUserProfile(userId, profileData)
    }
}