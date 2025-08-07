package com.judahben149.tala.domain.repository

import com.judahben149.tala.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(): Flow<List<ChatMessage>>
    suspend fun sendMessage(text: String): Result<Unit>
    suspend fun connectToChat()
    suspend fun disconnectFromChat()
}
