package com.judahben149.tala.domain.usecases.speech.recording

import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.exception.recording.AudioRecordingException
import com.judahben149.tala.domain.models.exception.recording.RecordingNotStartedException
import com.judahben149.tala.domain.models.speech.RecorderConfig
import com.judahben149.tala.domain.repository.AudioRepository

class StartRecordingUseCase(
    private val audioRepository: AudioRepository
) {
    suspend operator fun invoke(config: RecorderConfig = RecorderConfig()): Result<Unit, AudioRecordingException> {
        return try {
            audioRepository.startRecording(config)
        } catch (e: Exception) {
            Result.Failure(RecordingNotStartedException("Failed to start recording: ${e.message}"))
        }
    }
}