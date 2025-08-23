package com.judahben149.tala.domain.usecases.conversations

import com.judahben149.tala.domain.models.conversation.Conversation
import com.judahben149.tala.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow

class GetConversationByIdUseCase(
    private val repository: ConversationRepository
) {
    operator fun invoke(conversationId: String): Flow<Conversation?> {
        return repository.getConversationById(conversationId)
    }
}