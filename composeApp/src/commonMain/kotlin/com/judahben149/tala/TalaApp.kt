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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.navigation.RootComponent
import com.judahben149.tala.navigation.flow.MainFlow
import com.judahben149.tala.navigation.flow.OnboardingFlow
import org.koin.compose.koinInject

@Composable
fun TalaApp(rootComponent: RootComponent) {
    val sessionManager: SessionManager = koinInject()

    LaunchedEffect(Unit) {
        sessionManager.checkAppState()
    }

    val appState by sessionManager.appState.collectAsState()

    LaunchedEffect(appState) {
        if (appState != SessionManager.AppState.Unknown) {
            rootComponent.checkAuthenticationState()
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