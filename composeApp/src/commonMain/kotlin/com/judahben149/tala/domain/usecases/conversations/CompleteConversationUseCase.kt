package com.judahben149.tala.domain.usecases.conversations

import com.judahben149.tala.domain.repository.ConversationRepository
import com.judahben149.tala.domain.models.common.Result

class CompleteConversationUseCase(
    private val repository: ConversationRepository
) {
    suspend operator fun invoke(conversationId: String): Result<Unit, Exception> {
        return repository.completeConversation(conversationId)
    }
}