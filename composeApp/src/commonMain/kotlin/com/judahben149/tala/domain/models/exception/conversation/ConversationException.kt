package com.judahben149.tala.domain.models.exception.conversation

// Base exception for all conversation-related errors
open class ConversationException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)

// Database-related exceptions
class ConversationDatabaseException(
    message: String,
    cause: Throwable? = null
) : ConversationException("Database error: $message", cause)

class ConversationNotFoundException(
    conversationId: String,
    cause: Throwable? = null
) : ConversationException("Conversation not found: $conversationId", cause)

class MessageNotFoundException(
    messageId: String,
    cause: Throwable? = null
) : ConversationException("Message not found: $messageId", cause)

// Audio-related exceptions
class AudioStorageException(
    message: String,
    cause: Throwable? = null
) : ConversationException("Audio storage error: $message", cause)

class AudioFileNotFoundException(
    filePath: String,
    cause: Throwable? = null
) : ConversationException("Audio file not found: $filePath", cause)

class InvalidAudioDataException(
    message: String,
    cause: Throwable? = null
) : ConversationException("Invalid audio data: $message", cause)

// Validation exceptions
class InvalidConversationDataException(
    message: String,
    cause: Throwable? = null
) : ConversationException("Invalid conversation data: $message", cause)

class InvalidMessageDataException(
    message: String,
    cause: Throwable? = null
) : ConversationException("Invalid message data: $message", cause)

class InvalidVocabularyDataException(
    message: String,
    cause: Throwable? = null
) : ConversationException("Invalid vocabulary data: $message", cause)

// User-related exceptions
class UnauthorizedConversationAccessException(
    userId: String,
    conversationId: String,
    cause: Throwable? = null
) : ConversationException("User $userId not authorized to access conversation $conversationId", cause)
