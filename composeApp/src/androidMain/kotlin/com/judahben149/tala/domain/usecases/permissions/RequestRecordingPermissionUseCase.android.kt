package com.judahben149.tala.domain.usecases.permissions

import android.app.Activity
import androidx.core.app.ActivityCompat
import com.judahben149.tala.domain.models.common.Result
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class RequestRecordingPermissionUseCase(
    private val activity: Activity
) {
    actual suspend operator fun invoke(): Result<Boolean, Exception> {
        return try {
            val permission = android.Manifest.permission.RECORD_AUDIO

            // Check if permission is already granted
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    activity,
                    permission
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                return Result.Success(true)
            }

            // Request permission
            val result = suspendCancellableCoroutine { continuation ->
                val requestCode = PERMISSION_REQUEST_CODE

                // Store the continuation to be called from onRequestPermissionsResult
                permissionCallbacks[requestCode] = { granted ->
                    continuation.resume(granted)
                }

                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(permission),
                    requestCode
                )
            }

            Result.Success(result)
        } catch (e: Exception) {
            Result.Failure(Exception("Failed to request recording permission: ${e.message}", e))
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
        private val permissionCallbacks = mutableMapOf<Int, (Boolean) -> Unit>()

        // This should be called from your Activity's onRequestPermissionsResult
        fun handlePermissionResult(
            requestCode: Int,
            grantResults: IntArray
        ) {
            val callback = permissionCallbacks.remove(requestCode)
            if (callback != null) {
                val granted = grantResults.isNotEmpty() &&
                        grantResults[0] == android.content.pm.PackageManager.PERMISSION_GRANTED
                callback(granted)
            }
        }
    }
}