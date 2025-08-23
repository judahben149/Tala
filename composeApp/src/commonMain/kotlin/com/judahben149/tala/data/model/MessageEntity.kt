package com.judahben149.tala.data.model

data class MessageEntity(
    val id: String,
    val conversationId: String,
    val content: String,
    val isUser: Boolean,
    val timestamp: Long,
    val userAudioPath: String? = null,
    val aiAudioPath: String? = null,
    val correction: String? = null,
    val vocabularyHighlighted: String? = null,
    val grammarFeedback: String? = null,
    val responseTimeMs: Long? = null,
    val messageOrder: Int,
    val firestoreSyncStatus: Int = 0
)