package com.judahben149.tala.domain.mappers

import com.judahben149.tala.domain.models.exception.conversation.AudioStorageException
import com.judahben149.tala.domain.models.exception.conversation.ConversationDatabaseException
import com.judahben149.tala.domain.models.exception.conversation.ConversationException
import com.judahben149.tala.domain.models.exception.conversation.InvalidAudioDataException
import com.judahben149.tala.domain.models.exception.conversation.InvalidConversationDataException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

/**
 * Extension function to convert Throwable to appropriate ConversationException
 */
fun Throwable.toConversationException(): Exception {
    return when (this) {
        // Already an Exception, return as-is
        is Exception -> this

        // Fallback: wrap any other Throwable as ConversationException
        else -> ConversationException("Unexpected error: ${this.message ?: "Unknown error"}", this)
    }
}

/**
 * Extension function to convert database-related throwables to appropriate exceptions
 */
fun Throwable.toDatabaseException(): Exception {
    return when (this) {
        is IllegalArgumentException -> InvalidConversationDataException(this.message ?: "Invalid data", this)
        is IllegalStateException -> ConversationDatabaseException("Database in invalid state: ${this.message}", this)
        is RuntimeException -> ConversationDatabaseException("Runtime error: ${this.message}", this)
        is Exception -> this
        else -> ConversationDatabaseException("Database operation failed: ${this.message}", this)
    }
}

/**
 * Extension function to convert audio-related throwables to appropriate exceptions
 */
fun Throwable.toAudioException(): Exception {
    return when (this) {
        is IOException -> AudioStorageException("File I/O error: ${this.message}", this)
        is IllegalArgumentException -> InvalidAudioDataException(
            this.message ?: "Invalid audio data", this
        )
        is RuntimeException -> AudioStorageException("Runtime error during audio operation: ${this.message}", this)
        is Exception -> this
        else -> AudioStorageException("Audio operation failed: ${this.message}", this)
    }
}

/**
 * Extension function to convert serialization-related throwables to appropriate exceptions
 */
fun Throwable.toSerializationException(): Exception {
    return when (this) {
        is SerializationException -> InvalidConversationDataException(
            "Serialization failed: ${this.message}",
            this
        )
        is IllegalArgumentException -> InvalidConversationDataException(this.message ?: "Invalid data format", this)
        is Exception -> this
        else -> InvalidConversationDataException("Data conversion failed: ${this.message}", this)
    }
}

/**
 * Extension function for general conversation operation error mapping
 */
fun Throwable.toConversationOperationException(operation: String): Exception {
    return when (this) {
        is NullPointerException -> ConversationException("Null reference in $operation", this)
        is IllegalArgumentException -> InvalidConversationDataException("Invalid argument in $operation: ${this.message}", this)
        is IllegalStateException -> ConversationException(
            "Invalid state during $operation: ${this.message}",
            this
        )
        is ConcurrentModificationException -> ConversationException("Concurrent modification during $operation", this)
        is IndexOutOfBoundsException -> ConversationException("Index out of bounds in $operation: ${this.message}", this)
        is NoSuchElementException -> ConversationException("Element not found in $operation: ${this.message}", this)
        is NumberFormatException -> InvalidConversationDataException("Number format error in $operation: ${this.message}", this)
        is ArithmeticException -> ConversationException("Arithmetic error in $operation: ${this.message}", this)
        is Exception -> this
        else -> ConversationException("$operation failed: ${this.message ?: "Unknown error"}", this)
    }
}

/**
 * Extension function for validation-related errors
 */
fun Throwable.toValidationException(): Exception {
    return when (this) {
        is IllegalArgumentException -> InvalidConversationDataException(this.message ?: "Validation failed", this)
        is IllegalStateException -> InvalidConversationDataException("Invalid state: ${this.message}", this)
        is NullPointerException -> InvalidConversationDataException("Required field is null", this)
        is Exception -> this
        else -> InvalidConversationDataException("Validation error: ${this.message}", this)
    }
}

/**
 * Extension function for network/connection related errors
 */
fun Throwable.toNetworkException(): Exception {
    return when (this) {
        is IOException -> ConversationException("Network I/O error: ${this.message}", this)
        is RuntimeException -> ConversationException("Network runtime error: ${this.message}", this)
        is Exception -> this
        else -> ConversationException("Network operation failed: ${this.message}", this)
    }
}
