package com.judahben149.tala.domain.mappers

import com.judahben149.tala.data.model.ConversationEntity
import com.judahben149.tala.data.model.MessageEntity
import com.judahben149.tala.data.model.VocabularyItemEntity
import com.judahben149.tala.domain.models.conversation.Conversation
import com.judahben149.tala.domain.models.conversation.ConversationMessage
import com.judahben149.tala.domain.models.conversation.ConversationType
import com.judahben149.tala.domain.models.conversation.DifficultyLevel
import com.judahben149.tala.domain.models.conversation.MasteryLevel
import com.judahben149.tala.domain.models.conversation.VocabularyItem
import kotlinx.serialization.json.Json

fun ConversationEntity.toDomain() = Conversation(
    id = id,
    userId = userId,
    title = title,
    topic = topic,
    language = language,
    difficultyLevel = DifficultyLevel.valueOf(difficultyLevel.uppercase()),
    createdAt = createdAt,
    updatedAt = updatedAt,
    isCompleted = isCompleted,
    sessionDuration = sessionDuration,
    totalMessages = totalMessages,
    correctionsCount = correctionsCount,
    vocabularyLearned = vocabularyLearned,
    aiModelUsed = aiModelUsed,
    conversationType = ConversationType.valueOf(conversationType.uppercase())
)

fun Conversation.toEntity() = ConversationEntity(
    id = id,
    userId = userId,
    title = title,
    topic = topic,
    language = language,
    difficultyLevel = difficultyLevel.name.lowercase(),
    createdAt = createdAt,
    updatedAt = updatedAt,
    isCompleted = isCompleted,
    sessionDuration = sessionDuration,
    totalMessages = totalMessages,
    correctionsCount = correctionsCount,
    vocabularyLearned = vocabularyLearned,
    aiModelUsed = aiModelUsed,
    conversationType = conversationType.name.lowercase()
)

fun MessageEntity.toDomain() = ConversationMessage(
    id = id,
    conversationId = conversationId,
    content = content,
    isUser = isUser,
    timestamp = timestamp,
    userAudioPath = userAudioPath,
    aiAudioPath = aiAudioPath,
    correction = correction,
    vocabularyHighlighted = vocabularyHighlighted?.let {
        Json.decodeFromString<List<String>>(it)
    } ?: emptyList(),
    grammarFeedback = grammarFeedback,
    responseTimeMs = responseTimeMs,
    messageOrder = messageOrder
)

fun ConversationMessage.toEntity() = MessageEntity(
    id = id,
    conversationId = conversationId,
    content = content,
    isUser = isUser,
    timestamp = timestamp,
    userAudioPath = userAudioPath,
    aiAudioPath = aiAudioPath,
    correction = correction,
    vocabularyHighlighted = if (vocabularyHighlighted.isNotEmpty()) {
        Json.encodeToString(vocabularyHighlighted)
    } else null,
    grammarFeedback = grammarFeedback,
    responseTimeMs = responseTimeMs,
    messageOrder = messageOrder
)

fun VocabularyItemEntity.toDomain() = VocabularyItem(
    id = id,
    userId = userId,
    word = word,
    definition = definition,
    language = language,
    conversationId = conversationId,
    learnedAt = learnedAt,
    practiceCount = practiceCount,
    masteryLevel = MasteryLevel.entries.find { it.value == masteryLevel } ?: MasteryLevel.BEGINNER,
    contextSentence = contextSentence
)

fun VocabularyItem.toEntity() = VocabularyItemEntity(
    id = id,
    userId = userId,
    word = word,
    definition = definition,
    language = language,
    conversationId = conversationId,
    learnedAt = learnedAt,
    practiceCount = practiceCount,
    masteryLevel = masteryLevel.value,
    contextSentence = contextSentence
)