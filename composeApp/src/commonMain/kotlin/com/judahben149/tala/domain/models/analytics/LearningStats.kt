package com.judahben149.tala.domain.models.analytics

data class LearningStats(
    val totalConversations: Int,
    val completedConversations: Int,
    val totalMessages: Int,
    val totalVocabularyLearned: Int,
    val averageConversationLength: Double,
    val totalLearningTimeMinutes: Long,
    val currentStreak: Int,
    val correctionsReceived: Int
)