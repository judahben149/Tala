package com.judahben149.tala

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import com.judahben149.tala.data.service.SignInStateTracker
import com.judahben149.tala.navigation.RootComponent
import com.judahben149.tala.navigation.flow.MainFlow
import com.judahben149.tala.navigation.flow.OnboardingFlow
import org.koin.compose.koinInject

@Composable
fun TalaApp(rootComponent: RootComponent) {
    val signInStateTracker: SignInStateTracker = koinInject()

    LaunchedEffect(Unit) {
        signInStateTracker.checkSignInState()
    }

    // Listen to auth state changes and trigger navigation
    val isSignedIn by signInStateTracker.isSignedIn.collectAsState()
    LaunchedEffect(isSignedIn) {
        if (isSignedIn != null) {
            rootComponent.checkAuthenticationState()
        }
    }

    MaterialTheme {
        val childStack by rootComponent.childStack.subscribeAsState()

        Children(
            stack = childStack,
            animation = stackAnimation(slide())
        ) { child ->
            when (val instance = child.instance) {
                is RootComponent.RootChild.Loading -> LoadingScreen()
                is RootComponent.RootChild.Onboarding -> OnboardingFlow(instance.component)
                is RootComponent.RootChild.Main -> MainFlow(instance.component)
            }
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