package com.judahben149.tala.domain.usecases.permissions

import com.judahben149.tala.domain.models.common.Result
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionRecordPermissionDenied
import platform.AVFAudio.AVAudioSessionRecordPermissionGranted
import platform.AVFAudio.AVAudioSessionRecordPermissionUndetermined

actual class CheckRecordingPermissionUseCase {
    actual operator fun invoke(): Result<Boolean, Exception> {
        return try {
            val audioSession = AVAudioSession.sharedInstance()
            val status = audioSession.recordPermission()

            val hasPermission = when (status) {
                AVAudioSessionRecordPermissionGranted -> true
                else -> false
            }

            Result.Success(hasPermission)
        } catch (e: Exception) {
            Result.Failure(Exception("Failed to check recording permission: ${e.message}", e))
        }
    }
}
