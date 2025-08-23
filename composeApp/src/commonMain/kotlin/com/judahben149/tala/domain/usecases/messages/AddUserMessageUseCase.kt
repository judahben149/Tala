package com.judahben149.tala.domain.usecases.messages

import com.judahben149.tala.data.local.generateUUID
import com.judahben149.tala.data.local.getCurrentTimeMillis
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.conversation.ConversationMessage
import com.judahben149.tala.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.first

class AddUserMessageUseCase(
    private val repository: ConversationRepository
) {
    suspend operator fun invoke(
        conversationId: String,
        content: String,
        userAudioBase64: String? = null,
        responseTimeMs: Long? = null
    ): Result<Unit, Exception> {
        
        val messageOrder = getNextMessageOrder(conversationId)
        
        val message = ConversationMessage(
            id = generateUUID(),
            conversationId = conversationId,
            content = content,
            isUser = true,
            timestamp = getCurrentTimeMillis(),
            responseTimeMs = responseTimeMs,
            messageOrder = messageOrder
        )
        
        return if (userAudioBase64 != null) {
            repository.addMessageWithAudio(message, userAudioBase64, null)
        } else {
            repository.addMessage(message)
        }
    }
    
    private suspend fun getNextMessageOrder(conversationId: String): Int {
        val messages = repository.getMessagesByConversationId(conversationId).first()
        return messages.size + 1
    }
}