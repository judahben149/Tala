package com.judahben149.tala.domain.usecases.settings

import co.touchlab.kermit.Logger
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.user.Language
import com.judahben149.tala.domain.usecases.user.ObservePersistedUserDataUseCase

class GetLearningLanguageUseCase(
    private val logger: Logger,
    private val observePersistedUser: ObservePersistedUserDataUseCase
    ) {
    suspend operator fun invoke(): Result<Language, Exception> {
        return try {
            if (observePersistedUser.hasPersistedUser()) {
                val user = observePersistedUser.getCurrentUser()
                Result.Success(Language.valueOf(user.learningLanguage))
            } else {
                Result.Failure(Exception("No user data found"))
            }
        } catch (e: Exception) {
            logger.e { "Failed to get learning language: ${e.message}" }
            Result.Failure(e)
        }
    }
}