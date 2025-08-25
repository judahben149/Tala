package com.judahben149.tala.domain.usecases.settings

import com.judahben149.tala.domain.repository.UserRepository
import com.judahben149.tala.domain.models.common.Result

class GetLearningLanguageUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<String, Exception> {
        // Implementation will get learning language
        TODO("Implementation needed")
    }
}