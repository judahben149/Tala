package com.judahben149.tala.domain.usecases.conversations

import com.judahben149.tala.domain.models.conversation.Conversation
import com.judahben149.tala.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow

class GetConversationHistoryUseCase(
    private val repository: ConversationRepository
) {
    operator fun invoke(userId: String): Flow<List<Conversation>> {
        return repository.getConversationsByUserId(userId)
    }
}