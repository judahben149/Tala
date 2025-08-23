package com.judahben149.tala.data.model

data class VocabularyItemEntity(
    val id: String,
    val userId: String,
    val word: String,
    val definition: String,
    val language: String,
    val conversationId: String?,
    val learnedAt: Long,
    val practiceCount: Int = 0,
    val masteryLevel: Int = 0,
    val contextSentence: String? = null,
    val firestoreSyncStatus: Int = 0
)