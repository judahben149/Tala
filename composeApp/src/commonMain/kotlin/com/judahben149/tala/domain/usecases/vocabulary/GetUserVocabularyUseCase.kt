package com.judahben149.tala.domain.usecases.vocabulary

import com.judahben149.tala.domain.models.conversation.VocabularyItem
import com.judahben149.tala.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.Flow

class GetUserVocabularyUseCase(
    private val repository: ConversationRepository
) {
    operator fun invoke(userId: String): Flow<List<VocabularyItem>> {
        return repository.getVocabularyByUserId(userId)
    }
}