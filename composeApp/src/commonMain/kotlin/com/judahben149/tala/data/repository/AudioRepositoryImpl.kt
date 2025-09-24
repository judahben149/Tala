package com.judahben149.tala.data.repository

import co.touchlab.kermit.Logger
import com.judahben149.tala.data.service.audio.SpeechRecorder
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.exception.recording.AlreadyRecordingException
import com.judahben149.tala.domain.models.exception.recording.AudioPermissionException
import com.judahben149.tala.domain.models.exception.recording.AudioRecordingException
import com.judahben149.tala.domain.models.exception.recording.RecordingNotStartedException
import com.judahben149.tala.domain.models.speech.RecorderConfig
import com.judahben149.tala.domain.models.speech.RecorderStatus
import com.judahben149.tala.domain.repository.AudioRepository
import kotlinx.coroutines.flow.StateFlow

class AudioRepositoryImpl(
    private val speechRecorder: SpeechRecorder,
    private val logger: Logger
) : AudioRepository {
    
    override val recordingStatus: StateFlow<RecorderStatus> = speechRecorder.status
    override val audioLevel: StateFlow<Float> = speechRecorder.audioLevel
    override val peakLevel: StateFlow<Float> = speechRecorder.peakLevel

    override suspend fun startRecording(config: RecorderConfig): Result<Unit, AudioRecordingException> {
        return try {
            logger.d { "AudioRepository: Starting recording with config: $config" }
            
            if (recordingStatus.value == RecorderStatus.Recording) {
                return Result.Failure(AlreadyRecordingException("Recording is already in progress"))
            }
            
            speechRecorder.start(config)
            logger.d { "AudioRepository: Recording started successfully" }
            Result.Success(Unit)
            
        } catch (e: Exception) {
            logger.e(e) { "AudioRepository: Security exception - missing recording permission" }
            Result.Failure(AudioPermissionException("Recording permission not granted: ${e.message}"))
        } catch (e: Exception) {
            logger.e(e) { "AudioRepository: Failed to start recording" }
            Result.Failure(
                RecordingNotStartedException(
                    "Failed to start recording: ${e.message}"
                )
            )
        }
    }

    override suspend fun stopRecording(): Result<ByteArray, Exception> {
        return try {
            logger.d { "AudioRepository: Stopping recording" }
            
            if (recordingStatus.value != RecorderStatus.Recording) {
                logger.w { "AudioRepository: Attempted to stop recording when not recording" }
                return Result.Failure(Exception("No active recording to stop"))
            }
            
            val audioData = speechRecorder.stop()
            
            if (audioData.isEmpty()) {
                logger.w { "AudioRepository: No audio data recorded" }
                Result.Failure(Exception("No audio data was recorded"))
            } else {
                logger.d { "AudioRepository: Recording stopped successfully. Audio size: ${audioData.size} bytes" }
                Result.Success(audioData)
            }
            
        } catch (e: Exception) {
            logger.e(e) { "AudioRepository: Failed to stop recording" }
            Result.Failure(Exception("Failed to stop recording: ${e.message}", e))
        }
    }

    override suspend fun cancelRecording(): Result<Unit, Exception> {
        return try {
            logger.d { "AudioRepository: Cancelling recording" }
            
            speechRecorder.cancel()
            logger.d { "AudioRepository: Recording cancelled successfully" }
            Result.Success(Unit)
            
        } catch (e: Exception) {
            logger.e(e) { "AudioRepository: Failed to cancel recording" }
            Result.Failure(Exception("Failed to cancel recording: ${e.message}", e))
        }
    }

    override suspend fun isRecordingSupported(): Boolean {
        return try {
            // This is a simple check - you might want to make it more sophisticated
            // by actually testing if recording can be initialized
            true
        } catch (e: Exception) {
            logger.e(e) { "AudioRepository: Recording not supported" }
            false
        }
    }
}