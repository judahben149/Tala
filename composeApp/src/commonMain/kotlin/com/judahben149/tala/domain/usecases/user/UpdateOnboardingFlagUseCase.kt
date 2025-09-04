package com.judahben149.tala.domain.usecases.user

import com.judahben149.tala.domain.repository.UserRepository
import com.judahben149.tala.domain.models.common.Result

class UpdateOnboardingFlagUseCase(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(hasOnboarded: Boolean): Result<Unit, Exception> {
        return userRepository.updateOnboardingFlag(hasOnboarded)
    }
}