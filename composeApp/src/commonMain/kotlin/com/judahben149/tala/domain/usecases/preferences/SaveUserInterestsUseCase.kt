package com.judahben149.tala.domain.usecases.preferences

import com.judahben149.tala.domain.repository.UserRepository
import com.judahben149.tala.domain.models.common.Result

class SaveUserInterestsUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(interests: List<String>): Result<Unit, Exception> {
        return userRepository.saveUserInterests(interests)
    }
}