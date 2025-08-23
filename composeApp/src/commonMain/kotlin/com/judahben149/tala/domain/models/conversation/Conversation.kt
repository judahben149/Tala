package com.judahben149.tala.domain.models.conversation

data class Conversation(
    val id: String,
    val userId: String,
    val title: String,
    val topic: String?,
    val language: String,
    val difficultyLevel: DifficultyLevel,
    val createdAt: Long,
    val updatedAt: Long,
    val isCompleted: Boolean = false,
    val sessionDuration: Long = 0,
    val totalMessages: Int = 0,
    val correctionsCount: Int = 0,
    val vocabularyLearned: Int = 0,
    val aiModelUsed: String = "gemini",
    val conversationType: ConversationType = ConversationType.AI_LEARNING
)