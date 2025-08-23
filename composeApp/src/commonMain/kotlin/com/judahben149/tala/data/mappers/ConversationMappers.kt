package com.judahben149.tala.data.mappers

import com.judahben149.tala.Conversations
import com.judahben149.tala.Messages
import com.judahben149.tala.Vocabulary_items
import com.judahben149.tala.data.model.ConversationEntity
import com.judahben149.tala.data.model.MessageEntity
import com.judahben149.tala.data.model.VocabularyItemEntity

// Mapping extensions
fun Conversations.toConversationEntity() = ConversationEntity(
    id = id,
    userId = user_id,
    title = title,
    topic = topic,
    language = language,
    difficultyLevel = difficulty_level,
    createdAt = created_at,
    updatedAt = updated_at,
    isCompleted = is_completed == 1L,
    sessionDuration = session_duration ?: 0,
    totalMessages = total_messages?.toInt() ?: 0,
    correctionsCount = corrections_count?.toInt() ?: 0,
    vocabularyLearned = vocabulary_learned?.toInt() ?: 0,
    aiModelUsed = ai_model_used ?: "gemini",
    conversationType = conversation_type ?: "ai_learning",
    firestoreSyncStatus = firestore_sync_status?.toInt() ?: 0,
    lastSyncedAt = last_synced_at
)

fun Messages.toMessageEntity() = MessageEntity(
    id = id,
    conversationId = conversation_id,
    content = content,
    isUser = is_user == 1L,
    timestamp = timestamp,
    userAudioPath = user_audio_path,
    aiAudioPath = ai_audio_path,
    correction = correction,
    vocabularyHighlighted = vocabulary_highlighted,
    grammarFeedback = grammar_feedback,
    responseTimeMs = response_time_ms,
    messageOrder = message_order.toInt(),
    firestoreSyncStatus = firestore_sync_status?.toInt() ?: 0
)

fun Vocabulary_items.toVocabularyItemEntity() = VocabularyItemEntity(
    id = id,
    userId = user_id,
    word = word,
    definition = definition,
    language = language,
    conversationId = conversation_id,
    learnedAt = learned_at,
    practiceCount = practice_count?.toInt() ?: 0,
    masteryLevel = mastery_level?.toInt() ?: 0,
    contextSentence = context_sentence,
    firestoreSyncStatus = firestore_sync_status?.toInt() ?: 0
)
