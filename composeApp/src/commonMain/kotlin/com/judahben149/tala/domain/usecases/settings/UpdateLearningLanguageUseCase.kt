package com.judahben149.tala.domain.usecases.settings

import com.judahben149.tala.domain.repository.UserRepository
import com.judahben149.tala.domain.models.common.Result

class UpdateLearningLanguageUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(language: String): Result<Unit, Exception> {
        // Implementation will update learning language
        TODO("Implementation needed")
    }
}