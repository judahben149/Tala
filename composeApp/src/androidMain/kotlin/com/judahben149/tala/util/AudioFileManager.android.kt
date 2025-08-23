package com.judahben149.tala.util

import android.content.Context
import android.util.Base64
import co.touchlab.kermit.Logger
import java.io.File

actual class AudioFileManager(
    private val context: Context
) {
    private val logger = Logger.withTag("AudioFileManager")

    private fun getAudioDirectory(): File {
        val dir = File(context.filesDir, "tala_audio")
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    private fun getConversationDirectory(conversationId: String): File {
        val dir = File(getAudioDirectory(), conversationId)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    actual suspend fun saveUserAudio(conversationId: String, messageId: String, base64Audio: String): String {
        return saveAudioFile(conversationId, messageId, base64Audio, "user")
    }

    actual suspend fun saveAiAudio(conversationId: String, messageId: String, base64Audio: String): String {
        return saveAudioFile(conversationId, messageId, base64Audio, "ai")
    }

    private suspend fun saveAudioFile(
        conversationId: String,
        messageId: String,
        base64Audio: String,
        prefix: String
    ): String {
        try {
            val audioBytes = Base64.decode(base64Audio, Base64.DEFAULT)
            val filename = "${prefix}_${messageId}.wav"
            val conversationDir = getConversationDirectory(conversationId)
            val audioFile = File(conversationDir, filename)

            audioFile.writeBytes(audioBytes)
            logger.d("Audio file saved: ${audioFile.absolutePath}")

            return audioFile.absolutePath
        } catch (e: Exception) {
            logger.e("Failed to save audio file", e)
            throw e
        }
    }

    actual suspend fun getAudioFile(filePath: String): ByteArray? {
        return try {
            val file = File(filePath)
            if (file.exists()) {
                file.readBytes()
            } else {
                logger.w("Audio file not found: $filePath")
                null
            }
        } catch (e: Exception) {
            logger.e("Failed to read audio file: $filePath", e)
            null
        }
    }

    actual suspend fun deleteAudioFile(filePath: String): Boolean {
        return try {
            val file = File(filePath)
            val result = file.delete()
            if (result) {
                logger.d("Audio file deleted: $filePath")
            } else {
                logger.w("Failed to delete audio file: $filePath")
            }
            result
        } catch (e: Exception) {
            logger.e("Error deleting audio file: $filePath", e)
            false
        }
    }

    actual suspend fun deleteConversationAudio(conversationId: String): Boolean {
        return try {
            val conversationDir = getConversationDirectory(conversationId)
            val result = conversationDir.deleteRecursively()
            if (result) {
                logger.d("Conversation audio deleted: $conversationId")
            } else {
                logger.w("Failed to delete conversation audio: $conversationId")
            }
            result
        } catch (e: Exception) {
            logger.e("Error deleting conversation audio: $conversationId", e)
            false
        }
    }
}
