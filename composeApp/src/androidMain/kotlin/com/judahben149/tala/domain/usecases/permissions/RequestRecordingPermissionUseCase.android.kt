package com.judahben149.tala.domain.usecases.permissions

import android.app.Activity
import android.content.Context
import androidx.core.app.ActivityCompat
import com.judahben149.tala.domain.models.common.Result
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

// In androidMain
actual class RequestRecordingPermissionUseCase(
    private val context: Context
) {
    actual suspend operator fun invoke(): Result<Boolean, Exception> {
        return try {
            val permission = android.Manifest.permission.RECORD_AUDIO

            // Check if permission is already granted
            val isGranted = androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                permission
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED

            if (isGranted) {
                Result.Success(true)
            } else {
                Result.Success(false)
            }
        } catch (e: Exception) {
            Result.Failure(Exception("Failed to check recording permission: ${e.message}", e))
        }
    }
}
