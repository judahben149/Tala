package com.judahben149.tala.data.local

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import app.cash.sqldelight.coroutines.mapToOneOrNull
import com.judahben149.tala.TalaDatabase
import com.judahben149.tala.data.model.ConversationEntity
import com.judahben149.tala.data.model.MessageEntity
import com.judahben149.tala.data.model.VocabularyItemEntity
import com.judahben149.tala.data.mappers.toConversationEntity
import com.judahben149.tala.data.mappers.toMessageEntity
import com.judahben149.tala.data.mappers.toVocabularyItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ConversationDatabaseHelper(driverFactory: DatabaseDriverFactory) {
    private val database: TalaDatabase = TalaDatabase(driverFactory.createDriver())
    private val conversationQueries = database.conversationsQueries
    private val messageQueries = database.messagesQueries
    private val vocabularyQueries = database.vocabularyItemsQueries

    // Conversation operations
    fun getConversationById(conversationId: String): Flow<ConversationEntity?> {
        return conversationQueries.getConversationById(conversationId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toConversationEntity() }
    }

    fun getConversationsByUserId(userId: String): Flow<List<ConversationEntity>> {
        return conversationQueries.getConversationsByUserId(userId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { conversations -> conversations.map { it.toConversationEntity() } }
    }

    fun getActiveConversation(userId: String): Flow<ConversationEntity?> {
        return conversationQueries.getActiveConversation(userId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toConversationEntity() }
    }

    suspend fun insertConversation(conversation: ConversationEntity) {
        conversationQueries.insertConversation(
            id = conversation.id,
            user_id = conversation.userId,
            title = conversation.title,
            topic = conversation.topic,
            language = conversation.language,
            difficulty_level = conversation.difficultyLevel,
            created_at = conversation.createdAt,
            updated_at = conversation.updatedAt,
            conversation_type = conversation.conversationType
        )
    }

    suspend fun updateConversation(conversation: ConversationEntity) {
        conversationQueries.updateConversation(
            title = conversation.title,
            topic = conversation.topic,
            difficulty_level = conversation.difficultyLevel,
            updated_at = conversation.updatedAt,
            is_completed = if (conversation.isCompleted) 1L else 0L,
            session_duration = conversation.sessionDuration,
            total_messages = conversation.totalMessages.toLong(),
            corrections_count = conversation.correctionsCount.toLong(),
            vocabulary_learned = conversation.vocabularyLearned.toLong(),
            id = conversation.id
        )
    }

    suspend fun markConversationCompleted(conversationId: String, timestamp: Long) {
        conversationQueries.markConversationCompleted(timestamp, conversationId)
    }

    // Message operations
    fun getMessagesByConversationId(conversationId: String): Flow<List<MessageEntity>> {
        return messageQueries.getMessagesByConversationId(conversationId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { messages -> messages.map { it.toMessageEntity() } }
    }

    suspend fun insertMessage(message: MessageEntity) {
        messageQueries.insertMessage(
            id = message.id,
            conversation_id = message.conversationId,
            content = message.content,
            is_user = if (message.isUser) 1L else 0L,
            timestamp = message.timestamp,
            user_audio_path = message.userAudioPath,
            ai_audio_path = message.aiAudioPath,
            correction = message.correction,
            vocabulary_highlighted = message.vocabularyHighlighted,
            grammar_feedback = message.grammarFeedback,
            response_time_ms = message.responseTimeMs,
            message_order = message.messageOrder.toLong()
        )
    }

    // Vocabulary operations
    fun getVocabularyByUserId(userId: String): Flow<List<VocabularyItemEntity>> {
        return vocabularyQueries.getVocabularyByUserId(userId)
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { items -> items.map { it.toVocabularyItemEntity() } }
    }

    suspend fun insertVocabularyItem(item: VocabularyItemEntity) {
        vocabularyQueries.insertVocabularyItem(
            id = item.id,
            user_id = item.userId,
            word = item.word,
            definition = item.definition,
            language = item.language,
            conversation_id = item.conversationId,
            learned_at = item.learnedAt,
            context_sentence = item.contextSentence
        )
    }
}
