package com.judahben149.tala.domain.usecases.conversations

import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.util.AudioFileManager
import co.touchlab.kermit.Logger

class GetAudioFileUseCase(
    private val audioFileManager: AudioFileManager,
    private val logger: Logger
) {
    suspend operator fun invoke(filePath: String): Result<ByteArray, Exception> {
        return try {
            val audioBytes = audioFileManager.getAudioFile(filePath)
            if (audioBytes != null) {
                logger.d { "Audio file retrieved successfully: $filePath" }
                Result.Success(audioBytes)
            } else {
                val error = Exception("Audio file not found: $filePath")
                logger.w { error.message.toString() }
                Result.Failure(error)
            }
        } catch (e: Exception) {
            logger.e(e) { "Failed to get audio file: $filePath" }
            Result.Failure(e)
        }
    }
}