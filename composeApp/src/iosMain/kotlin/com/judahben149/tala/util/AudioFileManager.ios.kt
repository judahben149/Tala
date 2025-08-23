package com.judahben149.tala.util

import co.touchlab.kermit.Logger
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.create
import platform.Foundation.dataWithContentsOfURL
import platform.Foundation.writeToURL
import platform.posix.memcpy

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual class AudioFileManager {
    private val logger = Logger.withTag("AudioFileManager")

    private fun getDocumentsDirectory(): NSURL? {
        return NSFileManager.defaultManager.URLsForDirectory(
            NSDocumentDirectory,
            NSUserDomainMask
        ).firstOrNull() as? NSURL
    }

    private fun getAudioDirectory(): NSURL? {
        val documentsDir = getDocumentsDirectory() ?: return null
        val audioDir = documentsDir.URLByAppendingPathComponent("tala_audio", true)

        val fileManager = NSFileManager.defaultManager
        val path = audioDir?.path ?: return null

        if (!fileManager.fileExistsAtPath(path)) {
            try {
                fileManager.createDirectoryAtURL(
                    audioDir,
                    withIntermediateDirectories = true,
                    attributes = null,
                    error = null
                )
            } catch (e: Throwable) {
                logger.e("Failed to create audio directory", e)
                return null
            }
        }

        return audioDir
    }

    private fun getConversationDirectory(conversationId: String): NSURL? {
        val audioDir = getAudioDirectory() ?: return null
        val conversationDir = audioDir.URLByAppendingPathComponent(conversationId, true)

        val fileManager = NSFileManager.defaultManager
        val path = conversationDir?.path ?: return null

        if (!fileManager.fileExistsAtPath(path)) {
            try {
                fileManager.createDirectoryAtURL(
                    conversationDir,
                    withIntermediateDirectories = true,
                    attributes = null,
                    error = null
                )
            } catch (e: Throwable) {
                logger.e("Failed to create conversation directory", e)
                return null
            }
        }

        return conversationDir
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
            val conversationDir = getConversationDirectory(conversationId)
                ?: throw IllegalStateException("Could not create conversation directory")

            val filename = "${prefix}_${messageId}.wav"
            val fileURL = conversationDir.URLByAppendingPathComponent(filename, false)
                ?: throw IllegalStateException("Could not create file URL for $filename")

            val audioData = NSData.create(base64EncodedString = base64Audio, options = 0u)
                ?: throw IllegalArgumentException("Invalid base64 audio data")

            val success = audioData.writeToURL(fileURL, atomically = true)
            if (!success) {
                throw IllegalStateException("Failed to write audio file")
            }

            val filePath = fileURL.path ?: throw IllegalStateException("Could not get file path")
            logger.d("Audio file saved: $filePath")

            return filePath
        } catch (e: Throwable) {
            logger.e("Failed to save audio file", e)
            throw e
        }
    }


    actual suspend fun getAudioFile(filePath: String): ByteArray? {
        return try {
            val fileURL = NSURL.fileURLWithPath(filePath)
            val data = NSData.dataWithContentsOfURL(fileURL) ?: return null
            data.toByteArray()
        } catch (e: Throwable) {
            logger.e("Failed to read audio file: $filePath", e)
            null
        }
    }

    actual suspend fun deleteAudioFile(filePath: String): Boolean {
        return try {
            val fileURL = NSURL.fileURLWithPath(filePath)
            val fileManager = NSFileManager.defaultManager
            val success = fileManager.removeItemAtURL(fileURL, error = null)
            if (success) {
                logger.d("Audio file deleted: $filePath")
            } else {
                logger.w("Failed to delete audio file: $filePath")
            }
            success
        } catch (e: Throwable) {
            logger.e("Error deleting audio file: $filePath", e)
            false
        }
    }

    actual suspend fun deleteConversationAudio(conversationId: String): Boolean {
        return try {
            val conversationDir = getConversationDirectory(conversationId)
            if (conversationDir != null) {
                val fileManager = NSFileManager.defaultManager
                val success = fileManager.removeItemAtURL(conversationDir, error = null)
                if (success) {
                    logger.d("Conversation audio deleted: $conversationId")
                } else {
                    logger.w("Failed to delete conversation audio: $conversationId")
                }
                success
            } else {
                true // Directory doesn't exist, consider it deleted
            }
        } catch (e: Throwable) {
            logger.e("Error deleting conversation audio: $conversationId", e)
            false
        }
    }
}

// Extension function to convert NSData to ByteArray
@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray = ByteArray(length.toInt()).apply {
    usePinned {
        memcpy(it.addressOf(0), bytes, length)
    }
}
