package com.judahben149.tala.domain.usecases.vocabulary

import com.judahben149.tala.domain.models.conversation.VocabularyItem
import com.judahben149.tala.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.first

class GetRecentVocabularyUseCase(
    private val repository: ConversationRepository
) {
    suspend operator fun invoke(userId: String, limit: Int = 10): List<VocabularyItem> {
        return repository.getVocabularyByUserId(userId)
            .first()
            .sortedByDescending { it.learnedAt }
            .take(limit)
    }
}