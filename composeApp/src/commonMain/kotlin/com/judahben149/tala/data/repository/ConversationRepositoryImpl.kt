package com.judahben149.tala.data.repository

import co.touchlab.kermit.Logger
import com.judahben149.tala.data.local.ConversationDatabaseHelper
import com.judahben149.tala.data.local.getCurrentTimeMillis
import com.judahben149.tala.domain.mappers.toAudioException
import com.judahben149.tala.domain.mappers.toConversationOperationException
import com.judahben149.tala.domain.mappers.toDatabaseException
import com.judahben149.tala.domain.mappers.toDomain
import com.judahben149.tala.domain.mappers.toEntity
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.conversation.*
import com.judahben149.tala.domain.repository.ConversationRepository
import com.judahben149.tala.util.AudioFileManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ConversationRepositoryImpl(
    private val conversationDbHelper: ConversationDatabaseHelper,
    private val audioFileManager: AudioFileManager,
    private val logger: Logger
) : ConversationRepository {

    override fun getConversationById(conversationId: String): Flow<Conversation?> {
        return conversationDbHelper.getConversationById(conversationId)
            .map { it?.toDomain() }
    }

    override fun getConversationsByUserId(userId: String): Flow<List<Conversation>> {
        return conversationDbHelper.getConversationsByUserId(userId)
            .map { conversations -> conversations.map { it.toDomain() } }
    }

    override fun getActiveConversation(userId: String): Flow<Conversation?> {
        return conversationDbHelper.getActiveConversation(userId)
            .map { it?.toDomain() }
    }

    override suspend fun createConversation(conversation: Conversation): Result<String, Exception> {
        return runCatching {
            conversationDbHelper.insertConversation(conversation.toEntity())
            logger.d("Conversation created successfully: ${conversation.id}")
            conversation.id
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = {
                logger.e("Failed to create conversation: ${conversation.id}", it)
                Result.Failure(it.toDatabaseException())
            }
        )
    }

    override suspend fun updateConversation(conversation: Conversation): Result<Unit, Exception> {
        return runCatching {
            conversationDbHelper.updateConversation(conversation.toEntity())
            logger.d("Conversation updated successfully: ${conversation.id}")
        }.fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = {
                logger.e("Failed to update conversation: ${conversation.id}", it)
                Result.Failure(it.toDatabaseException())
            }
        )
    }

    override suspend fun completeConversation(conversationId: String): Result<Unit, Exception> {
        return runCatching {
            val timestamp = getCurrentTimeMillis()
            conversationDbHelper.markConversationCompleted(conversationId, timestamp)
            logger.d("Conversation completed successfully: $conversationId")
        }.fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = {
                logger.e("Failed to complete conversation: $conversationId", it)
                Result.Failure(it.toDatabaseException())
            }
        )
    }

    override fun getMessagesByConversationId(conversationId: String): Flow<List<ConversationMessage>> {
        return conversationDbHelper.getMessagesByConversationId(conversationId)
            .map { messages -> messages.map { it.toDomain() } }
    }

    override suspend fun addMessage(message: ConversationMessage): Result<Unit, Exception> {
        return runCatching {
            conversationDbHelper.insertMessage(message.toEntity())
            logger.d("Message added successfully: ${message.id}")
        }.fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = {
                logger.e("Failed to add message: ${message.id}", it)
                Result.Failure(it.toDatabaseException())
            }
        )
    }

    override suspend fun addMessageWithAudio(
        message: ConversationMessage,
        userAudioBase64: String?,
        aiAudioBase64: String?
    ): Result<Unit, Exception> {
        return runCatching {
            // Save audio files and get paths with specific error handling
            val userAudioPath = userAudioBase64?.let { base64Audio ->
                try {
                    audioFileManager.saveUserAudio(message.conversationId, message.id, base64Audio)
                        .also { path -> logger.d("User audio saved at: $path") }
                } catch (e: Throwable) {
                    logger.e("Failed to save user audio", e)
                    throw e.toAudioException()
                }
            }

            val aiAudioPath = aiAudioBase64?.let { base64Audio ->
                try {
                    audioFileManager.saveAiAudio(message.conversationId, message.id, base64Audio)
                        .also { path -> logger.d("AI audio saved at: $path") }
                } catch (e: Throwable) {
                    logger.e("Failed to save AI audio", e)
                    throw e.toAudioException()
                }
            }

            // Create message with audio paths
            val messageWithAudio = message.copy(
                userAudioPath = userAudioPath,
                aiAudioPath = aiAudioPath
            )

            conversationDbHelper.insertMessage(messageWithAudio.toEntity())
            logger.d("Message with audio added successfully: ${message.id}")
        }.fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = {
                logger.e("Failed to add message with audio: ${message.id}", it)
                // Check if it's already a converted exception, otherwise convert appropriately
                val convertedException = when {
                    it.message?.contains("audio", ignoreCase = true) == true -> it.toAudioException()
                    else -> it.toDatabaseException()
                }
                Result.Failure(convertedException)
            }
        )
    }

    override fun getVocabularyByUserId(userId: String): Flow<List<VocabularyItem>> {
        return conversationDbHelper.getVocabularyByUserId(userId)
            .map { items -> items.map { it.toDomain() } }
    }

    override suspend fun addVocabularyItem(item: VocabularyItem): Result<Unit, Exception> {
        return runCatching {
            conversationDbHelper.insertVocabularyItem(item.toEntity())
            logger.d("Vocabulary item added successfully: ${item.word}")
        }.fold(
            onSuccess = { Result.Success(Unit) },
            onFailure = {
                logger.e("Failed to add vocabulary item: ${item.word}", it)
                Result.Failure(it.toConversationOperationException("add vocabulary item"))
            }
        )
    }
}
