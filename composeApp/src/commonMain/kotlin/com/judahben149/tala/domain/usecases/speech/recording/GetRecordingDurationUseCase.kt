package com.judahben149.tala.domain.usecases.speech.recording

import com.judahben149.tala.domain.models.speech.RecorderStatus
import com.judahben149.tala.domain.repository.AudioRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class GetRecordingDurationUseCase(
    private val audioRepository: AudioRepository
) {
    @OptIn(ExperimentalTime::class)
    operator fun invoke(): Flow<Long> = flow {
        var startTime = 0L

        audioRepository.recordingStatus.collectLatest { status ->
            when (status) {
                RecorderStatus.Recording -> {
                    if (startTime == 0L) {
                        startTime = Clock.System.now().toEpochMilliseconds()
                    }
                    // emit duration updates until status changes
                    while (status == RecorderStatus.Recording) {
                        val duration = Clock.System.now().toEpochMilliseconds() - startTime
                        emit(duration)
                        delay(100)
                    }
                }
                else -> {
                    startTime = 0L
                    emit(0L)
                }
            }
        }
    }
}
