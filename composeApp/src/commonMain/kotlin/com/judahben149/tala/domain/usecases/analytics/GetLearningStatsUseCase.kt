package com.judahben149.tala.domain.usecases.analytics

import com.judahben149.tala.data.local.getCurrentTimeMillis
import com.judahben149.tala.domain.models.analytics.LearningStats
import com.judahben149.tala.domain.models.conversation.Conversation
import com.judahben149.tala.domain.repository.ConversationRepository
import kotlinx.coroutines.flow.first

class GetLearningStatsUseCase(
    private val repository: ConversationRepository
) {
    suspend operator fun invoke(userId: String): LearningStats {
        val conversations = repository.getConversationsByUserId(userId).first()
        val vocabulary = repository.getVocabularyByUserId(userId).first()
        
        val totalConversations = conversations.size
        val completedConversations = conversations.count { it.isCompleted }
        val totalMessages = conversations.sumOf { it.totalMessages }
        val totalVocabularyLearned = vocabulary.size
        val averageConversationLength = if (totalConversations > 0) {
            conversations.map { it.sessionDuration }.average() / 60_000.0 // Convert to minutes
        } else 0.0
        val totalLearningTime = conversations.sumOf { it.sessionDuration } / 60_000 // Convert to minutes
        val correctionsReceived = conversations.sumOf { it.correctionsCount }
        
        return LearningStats(
            totalConversations = totalConversations,
            completedConversations = completedConversations,
            totalMessages = totalMessages,
            totalVocabularyLearned = totalVocabularyLearned,
            averageConversationLength = averageConversationLength,
            totalLearningTimeMinutes = totalLearningTime,
            currentStreak = calculateCurrentStreak(conversations),
            correctionsReceived = correctionsReceived
        )
    }
    
    private fun calculateCurrentStreak(conversations: List<Conversation>): Int {
        // Simple streak calculation - days with at least one conversation
        val conversationDays = conversations
            .map { it.createdAt / (24 * 60 * 60 * 1000) } // Convert to days
            .distinct()
            .sorted()
            .reversed()
        
        if (conversationDays.isEmpty()) return 0
        
        val today = getCurrentTimeMillis() / (24 * 60 * 60 * 1000)
        var streak = 0
        var currentDay = today
        
        for (day in conversationDays) {
            if (day == currentDay) {
                streak++
                currentDay--
            } else {
                break
            }
        }
        
        return streak
    }
}