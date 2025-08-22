package com.judahben149.tala.domain.usecases.permissions

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.judahben149.tala.domain.models.common.Result

actual class CheckRecordingPermissionUseCase(
    private val context: Context
) {
    actual operator fun invoke(): Result<Boolean, Exception> {
        return try {
            val hasPermission = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED

            Result.Success(hasPermission)
        } catch (e: Exception) {
            Result.Failure(Exception("Failed to check recording permission: ${e.message}", e))
        }
    }
}