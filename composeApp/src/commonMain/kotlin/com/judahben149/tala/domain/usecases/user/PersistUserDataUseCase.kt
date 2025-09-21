package com.judahben149.tala.domain.usecases.user

import co.touchlab.kermit.Logger
import com.judahben149.tala.domain.models.user.AppUser
import com.judahben149.tala.domain.repository.UserRepository

class PersistUserDataUseCase(
    private val userRepository: UserRepository,
    private val logger: Logger
) {
    suspend operator fun invoke(user: AppUser) {
//        logger.d { "Persisting user data: $user" }
        userRepository.persistUserData(user)
    }
}