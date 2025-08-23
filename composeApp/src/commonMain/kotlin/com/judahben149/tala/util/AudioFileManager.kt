package com.judahben149.tala.util

expect class AudioFileManager {
    suspend fun saveUserAudio(conversationId: String, messageId: String, base64Audio: String): String
    suspend fun saveAiAudio(conversationId: String, messageId: String, base64Audio: String): String
    suspend fun getAudioFile(filePath: String): ByteArray?
    suspend fun deleteAudioFile(filePath: String): Boolean
    suspend fun deleteConversationAudio(conversationId: String): Boolean
}
