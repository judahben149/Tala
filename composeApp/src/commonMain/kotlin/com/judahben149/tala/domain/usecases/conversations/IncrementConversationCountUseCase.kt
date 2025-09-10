package com.judahben149.tala.domain.usecases.conversations

import com.judahben149.tala.domain.repository.UserRepository

class IncrementConversationCountUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke() {
        userRepository.incrementConversationCount()
    }
}