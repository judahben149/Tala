package com.judahben149.tala.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.judahben149.tala.data.service.permission.AudioPermissionManager
import com.judahben149.tala.domain.models.common.Result
import org.koin.compose.koinInject

@Composable
actual fun RequestAudioPermission(
    onPermissionResult: (Boolean) -> Unit
) {
    val permissionManager: AudioPermissionManager = koinInject()

    LaunchedEffect(Unit) {
        val result = permissionManager.ensurePermissionGranted()
        when (result) {
            is Result.Success -> onPermissionResult(result.data)
            is Result.Failure -> onPermissionResult(false)
        }
    }
}