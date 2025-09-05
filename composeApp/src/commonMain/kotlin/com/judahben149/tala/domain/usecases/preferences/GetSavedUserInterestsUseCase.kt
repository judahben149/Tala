package com.judahben149.tala.domain.usecases.preferences

import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.repository.UserRepository

class GetSavedUserInterestsUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): Result<List<String>, Exception> {
        return userRepository.getUserInterests()
    }
}