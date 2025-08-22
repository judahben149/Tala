package com.judahben149.tala.domain.usecases.permissions

import com.judahben149.tala.domain.models.common.Result
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFAudio.AVAudioSession
import kotlin.coroutines.resume

actual class RequestRecordingPermissionUseCase {
    actual suspend operator fun invoke(): Result<Boolean, Exception> {
        return try {
            val audioSession = AVAudioSession.sharedInstance()

            val result = suspendCancellableCoroutine { continuation ->
                audioSession.requestRecordPermission { granted ->
                    continuation.resume(granted)
                }
            }

            Result.Success(result)
        } catch (e: Exception) {
            Result.Failure(Exception("Failed to request recording permission: ${e.message}", e))
        }
    }
}