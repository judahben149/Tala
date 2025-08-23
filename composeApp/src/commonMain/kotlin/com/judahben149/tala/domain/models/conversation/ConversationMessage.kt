package com.judahben149.tala.domain.models.conversation

data class ConversationMessage(
    val id: String,
    val conversationId: String,
    val content: String,
    val isUser: Boolean,
    val timestamp: Long,
    val userAudioPath: String? = null,
    val aiAudioPath: String? = null,
    val correction: String? = null,
    val vocabularyHighlighted: List<String> = emptyList(),
    val grammarFeedback: String? = null,
    val responseTimeMs: Long? = null,
    val messageOrder: Int
)