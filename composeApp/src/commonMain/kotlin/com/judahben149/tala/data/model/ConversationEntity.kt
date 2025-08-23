package com.judahben149.tala.data.model

data class ConversationEntity(
    val id: String,
    val userId: String,
    val title: String,
    val topic: String?,
    val language: String,
    val difficultyLevel: String,
    val createdAt: Long,
    val updatedAt: Long,
    val isCompleted: Boolean = false,
    val sessionDuration: Long = 0,
    val totalMessages: Int = 0,
    val correctionsCount: Int = 0,
    val vocabularyLearned: Int = 0,
    val aiModelUsed: String = "gemini",
    val conversationType: String = "ai_learning",
    val firestoreSyncStatus: Int = 0,
    val lastSyncedAt: Long? = null
)