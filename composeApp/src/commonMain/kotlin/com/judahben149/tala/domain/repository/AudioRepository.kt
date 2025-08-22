package com.judahben149.tala.domain.repository

import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.exception.recording.AudioRecordingException
import com.judahben149.tala.domain.models.speech.RecorderConfig
import com.judahben149.tala.domain.models.speech.RecorderStatus
import kotlinx.coroutines.flow.StateFlow

interface AudioRepository {
    val recordingStatus: StateFlow<RecorderStatus>
    
    suspend fun startRecording(config: RecorderConfig = RecorderConfig()): Result<Unit, AudioRecordingException>
    suspend fun stopRecording(): Result<ByteArray, Exception>
    suspend fun cancelRecording(): Result<Unit, Exception>
    suspend fun isRecordingSupported(): Boolean
}