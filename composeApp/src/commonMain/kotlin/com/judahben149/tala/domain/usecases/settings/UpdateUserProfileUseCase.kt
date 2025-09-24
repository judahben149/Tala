package com.judahben149.tala.domain.usecases.settings

import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.user.AppUser
import com.judahben149.tala.domain.repository.UserRepository
import com.judahben149.tala.domain.usecases.user.PersistUserDataUseCase

class UpdateUserProfileUseCase(
    private val repository: UserRepository,
    private val persistUserDataUseCase: PersistUserDataUseCase
) {
    suspend operator fun invoke(appUser: AppUser): Result<AppUser, Exception> {
        return try {
            repository.updateUserProfile(appUser)
            persistUserDataUseCase(appUser)

            Result.Success(appUser)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }

    suspend fun saveUserProfile(userId: String, profileData: Map<String, Any>): Result<Unit, Exception> {
        return repository.saveUserProfile(userId, profileData)
    }
}