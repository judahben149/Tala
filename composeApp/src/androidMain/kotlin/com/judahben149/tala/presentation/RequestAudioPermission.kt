package com.judahben149.tala.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.judahben149.tala.ui.theme.getTalaColors


@OptIn(ExperimentalPermissionsApi::class, ExperimentalPermissionsApi::class)
@Composable
actual fun RequestAudioPermission(
    onPermissionResult: (Boolean) -> Unit
) {
    val permissionState = rememberPermissionState(
        permission = android.Manifest.permission.RECORD_AUDIO
    )

    LaunchedEffect(Unit) {
        when {
            permissionState.status.isGranted -> {
                onPermissionResult(true)
            }
            else -> {
                permissionState.launchPermissionRequest()
            }
        }
    }

    LaunchedEffect(permissionState.status) {
        onPermissionResult(permissionState.status.isGranted)
    }

    if (!permissionState.status.isGranted) {
        PermissionRationaleUI(
            permissionState = permissionState
        )
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
private fun PermissionRationaleUI(
    permissionState: PermissionState
) {
    val colors = getTalaColors()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = colors.cardBackground)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Microphone permission required")
            Button(
                onClick = { permissionState.launchPermissionRequest() }
            ) {
                Text("Grant Permission")
            }
        }
    }
}
