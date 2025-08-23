package com.judahben149.tala.domain.usecases.analytics

import com.judahben149.tala.data.local.getCurrentTimeMillis
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.first

class UpdateConversationStatsUseCase(
    private val repository: ConversationRepository
) {
    suspend operator fun invoke(
        conversationId: String,
        sessionDurationMs: Long,
        newMessagesCount: Int = 0,
        newCorrectionsCount: Int = 0,
        newVocabularyCount: Int = 0
    ): Result<Unit, Exception> {
        
        val conversation = repository.getConversationById(conversationId).first()
            ?: return Result.Failure(IllegalArgumentException("Conversation not found"))
        
        val updatedConversation = conversation.copy(
            updatedAt = getCurrentTimeMillis(),
            sessionDuration = conversation.sessionDuration + sessionDurationMs,
            totalMessages = conversation.totalMessages + newMessagesCount,
            correctionsCount = conversation.correctionsCount + newCorrectionsCount,
            vocabularyLearned = conversation.vocabularyLearned + newVocabularyCount
        )
        
        return repository.updateConversation(updatedConversation)
    }
}