package com.judahben149.tala.domain.model

import kotlinx.datetime.Instant

data class ChatMessage(
    val id: String,
    val text: String,
    val senderId: String,
    val senderName: String,
    val timestamp: Instant,
    val isFromCurrentUser: Boolean = false
)
