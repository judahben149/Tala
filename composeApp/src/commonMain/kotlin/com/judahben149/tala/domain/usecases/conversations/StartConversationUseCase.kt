package com.judahben149.tala.domain.usecases.conversations

import com.judahben149.tala.data.local.generateUUID
import com.judahben149.tala.data.local.getCurrentTimeMillis
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.conversation.Conversation
import com.judahben149.tala.domain.models.conversation.DifficultyLevel
import com.judahben149.tala.domain.repository.ConversationRepository

class StartConversationUseCase(
    private val repository: ConversationRepository
) {
    suspend operator fun invoke(
        userId: String,
        language: String,
        topic: String,
        difficultyLevel: DifficultyLevel = DifficultyLevel.BEGINNER
    ): Result<String, Exception> {
        val conversation = Conversation(
            id = generateUUID(),
            userId = userId,
            title = generateConversationTitle(topic, language),
            topic = topic,
            language = language,
            difficultyLevel = difficultyLevel,
            createdAt = getCurrentTimeMillis(),
            updatedAt = getCurrentTimeMillis()
        )
        
        return repository.createConversation(conversation)
    }
    
    private fun generateConversationTitle(topic: String, language: String): String {
        return when {
            topic.isNotBlank() -> "$language: $topic"
            else -> "$language Conversation"
        }
    }
}