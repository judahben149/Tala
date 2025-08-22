package com.judahben149.tala.domain.usecases.speech.recording

import com.judahben149.tala.domain.models.speech.RecorderStatus
import com.judahben149.tala.domain.repository.AudioRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveRecordingStatusUseCase(
    private val audioRepository: AudioRepository
) {
    operator fun invoke(): StateFlow<RecorderStatus> {
        return audioRepository.recordingStatus
    }
}