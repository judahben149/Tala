package com.judahben149.tala.domain.usecases.conversations

import com.judahben149.tala.domain.mappers.toMasteryLevel
import com.judahben149.tala.domain.models.conversation.MasteryLevel
import com.judahben149.tala.domain.repository.UserRepository

class GetMasteryLevelUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): MasteryLevel {
        return userRepository.getPersistedUser()?.currentLevel?.toMasteryLevel() ?: MasteryLevel.BEGINNER
    }
}