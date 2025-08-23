package com.judahben149.tala.domain.usecases.messages

import com.judahben149.tala.data.local.generateUUID
import com.judahben149.tala.data.local.getCurrentTimeMillis
import com.judahben149.tala.domain.models.conversation.ConversationMessage
import com.judahben149.tala.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.first
import com.judahben149.tala.domain.models.common.Result

class AddAiMessageUseCase(
    private val repository: ConversationRepository
) {
    suspend operator fun invoke(
        conversationId: String,
        content: String,
        aiAudioBase64: String? = null,
        correction: String? = null,
        vocabularyHighlighted: List<String> = emptyList(),
        grammarFeedback: String? = null,
        responseTimeMs: Long? = null
    ): Result<Unit, Exception> {
        
        val messageOrder = getNextMessageOrder(conversationId)
        
        val message = ConversationMessage(
            id = generateUUID(),
            conversationId = conversationId,
            content = content,
            isUser = false,
            timestamp = getCurrentTimeMillis(),
            correction = correction,
            vocabularyHighlighted = vocabularyHighlighted,
            grammarFeedback = grammarFeedback,
            responseTimeMs = responseTimeMs,
            messageOrder = messageOrder
        )
        
        return if (aiAudioBase64 != null) {
            repository.addMessageWithAudio(message, null, aiAudioBase64)
        } else {
            repository.addMessage(message)
        }
    }
    
    private suspend fun getNextMessageOrder(conversationId: String): Int {
        val messages = repository.getMessagesByConversationId(conversationId).first()
        return messages.size + 1
    }
}