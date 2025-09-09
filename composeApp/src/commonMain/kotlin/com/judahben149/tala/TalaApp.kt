package com.judahben149.tala

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import co.touchlab.kermit.Logger
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.judahben149.tala.domain.managers.FirebaseSyncManager
import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.navigation.RootComponent
import com.judahben149.tala.navigation.flow.MainFlow
import com.judahben149.tala.navigation.flow.OnboardingFlow
import com.mmk.kmpauth.google.GoogleAuthCredentials
import com.mmk.kmpauth.google.GoogleAuthProvider
import org.koin.compose.koinInject

@Composable
fun TalaApp(rootComponent: RootComponent) {
    val sessionManager: SessionManager = koinInject()
    val syncManager: FirebaseSyncManager = koinInject()
    val lifecycleOwner = LocalLifecycleOwner.current
    val logger: Logger = koinInject()

    LaunchedEffect(Unit) {
        GoogleAuthProvider.create(credentials = GoogleAuthCredentials(serverId = BuildKonfig.FIREBASE_WEB_CLIENT))
    }

    LaunchedEffect(Unit) {
        sessionManager.checkAppState()
    }

    // Handle app lifecycle for sync manager
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> {
                    logger.d { "Lifecycle --> STARTED" }
                    syncManager.startSyncing()
                }

                Lifecycle.Event.ON_STOP -> {
                    logger.d { "Lifecycle --> STOPPED" }
                    // Optionally pause syncing when app goes to background
                    // syncManager.stopSyncing()
                }

                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val appState by sessionManager.appState.collectAsState()
    val syncState by syncManager.syncState.collectAsState()

    LaunchedEffect(appState) {
        if (appState != SessionManager.AppState.Unknown) {
            rootComponent.checkAuthenticationState()
        }
    }

    LaunchedEffect(appState) {
        if (appState == SessionManager.AppState.LoggedIn) {
            syncManager.forceSyncNow()
        }
    }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            val childStack by rootComponent.childStack.subscribeAsState()

            Children(
                stack = childStack,
                animation = stackAnimation(slide()),
                modifier = Modifier.weight(1f)
            ) { child ->
                when (val instance = child.instance) {
                    is RootComponent.RootChild.Loading -> LoadingScreen()
                    is RootComponent.RootChild.Onboarding -> OnboardingFlow(instance.component)
                    is RootComponent.RootChild.Main -> MainFlow(instance.component)
                }
            }

            // Bottom spacer for navigation bar
            Spacer(
                modifier = Modifier.navigationBarsPadding()
            )
        }
    }
}


@Composable
private fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}