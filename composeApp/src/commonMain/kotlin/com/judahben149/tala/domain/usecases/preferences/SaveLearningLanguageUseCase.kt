package com.judahben149.tala.domain.usecases.preferences

import com.judahben149.tala.domain.repository.UserRepository
import com.judahben149.tala.domain.models.common.Result

class SaveLearningLanguageUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(language: String): Result<Unit, Exception> {
        return userRepository.saveLearningLanguage(language)
    }
}