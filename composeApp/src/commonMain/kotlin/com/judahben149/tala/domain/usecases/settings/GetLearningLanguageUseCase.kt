package com.judahben149.tala.domain.usecases.settings

import co.touchlab.kermit.Logger
import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.user.Language

class GetLearningLanguageUseCase(
    private val logger: Logger,
    private val sessionManager: SessionManager
    ) {
    suspend operator fun invoke(): Result<Language, Exception> {
        return try {
            Result.Success(sessionManager.getUserLanguagePreference())
        } catch (e: Exception) {
            logger.e { "Failed to get learning language: ${e.message}" }
            Result.Failure(e)
        }
    }
}