package com.judahben149.tala.domain.usecases.settings

import com.judahben149.tala.domain.repository.UserRepository
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.session.UserProfile

class GetUserProfileUseCase(
    private val repository: UserRepository
) {
    suspend operator fun invoke(): Result<UserProfile, Exception> {
        return repository.getUserProfile()
    }
}