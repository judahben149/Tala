package com.judahben149.tala.domain.usecases.speech.recording

import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.repository.AudioRepository

class CancelRecordingUseCase(
    private val audioRepository: AudioRepository
) {
    suspend operator fun invoke(): Result<Unit, Exception> {
        return try {
            audioRepository.cancelRecording()
        } catch (e: Exception) {
            Result.Failure(Exception("Failed to cancel recording: ${e.message}", e))
        }
    }
}