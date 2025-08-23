package com.judahben149.tala.domain.usecases.messages

import com.judahben149.tala.domain.models.conversation.ConversationMessage
import com.judahben149.tala.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow

class GetConversationMessagesUseCase(
    private val repository: ConversationRepository
) {
    operator fun invoke(conversationId: String): Flow<List<ConversationMessage>> {
        return repository.getMessagesByConversationId(conversationId)
    }
}