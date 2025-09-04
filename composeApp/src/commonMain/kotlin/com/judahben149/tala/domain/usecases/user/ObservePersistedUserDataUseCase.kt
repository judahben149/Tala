package com.judahben149.tala.domain.usecases.user

import com.judahben149.tala.domain.models.user.AppUser
import com.judahben149.tala.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow

class ObservePersistedUserDataUseCase(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<AppUser> {
        return userRepository.observePersistedUser()
    }

    suspend fun getCurrentUser(): AppUser {
        return userRepository.getPersistedUser()!!
    }

    suspend fun hasPersistedUser(): Boolean {
        return userRepository.getPersistedUser() != null
    }
}
