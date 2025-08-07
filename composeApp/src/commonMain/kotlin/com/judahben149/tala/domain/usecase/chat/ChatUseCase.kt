package com.judahben149.tala.domain.usecase.chat

import com.judahben149.tala.domain.model.ChatMessage
import com.judahben149.tala.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow

class ChatUseCase(
    private val chatRepository: ChatRepository
) {
    fun getMessages(): Flow<List<ChatMessage>> {
        return chatRepository.getMessages()
    }
    
    suspend fun sendMessage(text: String): Result<Unit> {
        return chatRepository.sendMessage(text)
    }
    
    suspend fun connectToChat() {
        chatRepository.connectToChat()
    }
    
    suspend fun disconnectFromChat() {
        chatRepository.disconnectFromChat()
    }
}
