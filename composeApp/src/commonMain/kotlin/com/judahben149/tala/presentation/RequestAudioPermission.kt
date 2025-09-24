package com.judahben149.tala.presentation

import androidx.compose.runtime.Composable

@Composable
expect fun RequestAudioPermission(
    onPermissionResult: (Boolean) -> Unit
)