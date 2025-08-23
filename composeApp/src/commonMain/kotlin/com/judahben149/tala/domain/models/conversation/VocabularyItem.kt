package com.judahben149.tala.domain.models.conversation

data class VocabularyItem(
    val id: String,
    val userId: String,
    val word: String,
    val definition: String,
    val language: String,
    val conversationId: String?,
    val learnedAt: Long,
    val practiceCount: Int = 0,
    val masteryLevel: MasteryLevel = MasteryLevel.BEGINNER,
    val contextSentence: String? = null
)