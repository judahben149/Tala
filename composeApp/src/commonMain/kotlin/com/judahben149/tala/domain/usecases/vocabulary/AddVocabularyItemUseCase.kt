package com.judahben149.tala.domain.usecases.vocabulary

import com.judahben149.tala.data.local.generateUUID
import com.judahben149.tala.data.local.getCurrentTimeMillis
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.conversation.VocabularyItem
import com.judahben149.tala.domain.repository.ConversationRepository

class AddVocabularyItemUseCase(
    private val repository: ConversationRepository
) {
    suspend operator fun invoke(
        userId: String,
        word: String,
        definition: String,
        language: String,
        conversationId: String? = null,
        contextSentence: String? = null
    ): Result<Unit, Exception> {
        
        val vocabularyItem = VocabularyItem(
            id = generateUUID(),
            userId = userId,
            word = word.trim().lowercase(),
            definition = definition.trim(),
            language = language,
            conversationId = conversationId,
            learnedAt = getCurrentTimeMillis(),
            contextSentence = contextSentence?.trim()
        )
        
        return repository.addVocabularyItem(vocabularyItem)
    }
}