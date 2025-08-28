package com.judahben149.tala

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.arkivanov.decompose.retainedComponent
import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.navigation.RootComponent
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        val sessionManager: SessionManager by inject()

        val rootComponent = retainedComponent { componentContext ->
            RootComponent(componentContext, sessionManager)
        }

        setContent {
            TalaApp(rootComponent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
//        RequestRecordingPermissionUseCase.handlePermissionResult(requestCode, grantResults)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
//    TalaApp()
}