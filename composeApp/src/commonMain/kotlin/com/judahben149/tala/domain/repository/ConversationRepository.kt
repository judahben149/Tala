package com.judahben149.tala.domain.repository

import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.conversation.Conversation
import com.judahben149.tala.domain.models.conversation.ConversationMessage
import com.judahben149.tala.domain.models.conversation.VocabularyItem
import kotlinx.coroutines.flow.Flow

interface ConversationRepository {
    fun getConversationById(conversationId: String): Flow<Conversation?>
    fun getConversationsByUserId(userId: String): Flow<List<Conversation>>
    fun getActiveConversation(userId: String): Flow<Conversation?>
    suspend fun createConversation(conversation: Conversation): Result<String, Exception>
    suspend fun updateConversation(conversation: Conversation): Result<Unit, Exception>
    suspend fun completeConversation(conversationId: String): Result<Unit, Exception>

    fun getMessagesByConversationId(conversationId: String): Flow<List<ConversationMessage>>
    suspend fun addMessage(message: ConversationMessage): Result<Unit, Exception>
    suspend fun addMessageWithAudio(
        message: ConversationMessage,
        userAudioBase64: String?,
        aiAudioBase64: String?
    ): Result<Unit, Exception>

    fun getVocabularyByUserId(userId: String): Flow<List<VocabularyItem>>
    suspend fun addVocabularyItem(item: VocabularyItem): Result<Unit, Exception>
}
