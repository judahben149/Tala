package com.judahben149.tala.domain.usecases.analytics

import com.judahben149.tala.data.local.getCurrentTimeMillis
import com.judahben149.tala.domain.models.analytics.WeeklyProgress
import com.judahben149.tala.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.first

class GetWeeklyProgressUseCase(
    private val repository: ConversationRepository
) {
    suspend operator fun invoke(userId: String): WeeklyProgress {
        val conversations = repository.getConversationsByUserId(userId).first()
        val vocabulary = repository.getVocabularyByUserId(userId).first()
        
        val oneWeekAgo = getCurrentTimeMillis() - (7 * 24 * 60 * 60 * 1000)
        
        val weeklyConversations = conversations.filter { it.createdAt >= oneWeekAgo }
        val weeklyVocabulary = vocabulary.filter { it.learnedAt >= oneWeekAgo }
        
        return WeeklyProgress(
            conversationsThisWeek = weeklyConversations.size,
            minutesThisWeek = weeklyConversations.sumOf { it.sessionDuration } / 60_000,
            newVocabularyThisWeek = weeklyVocabulary.size,
            averageDailyMinutes = (weeklyConversations.sumOf { it.sessionDuration } / 60_000) / 7.0
        )
    }
}