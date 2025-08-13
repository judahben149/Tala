package com.judahben149.tala

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.judahben149.tala.data.service.SignInStateTracker
import com.judahben149.tala.navigation.RootComponent
import com.judahben149.tala.presentation.screens.HomeScreen
import com.judahben149.tala.presentation.screens.login.LoginScreen
import com.judahben149.tala.presentation.screens.signUp.SignUpScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.koinInject

@Composable
@Preview
fun TalaApp(
    rootComponent: RootComponent
) {
    val signInStateTracker: SignInStateTracker = koinInject()

    LaunchedEffect(Unit) {
        signInStateTracker.checkSignInState()
    }

    val isSignedIn by signInStateTracker.isSignedIn.collectAsState()
    val hasSignedInBefore = remember { signInStateTracker.hasSignedInBefore() }

    // Navigate to appropriate screen based on auth state
    LaunchedEffect(isSignedIn, hasSignedInBefore) {
        when (isSignedIn) {
            true -> {
                rootComponent.navigateToHome()
            }
            false -> {
                if (hasSignedInBefore) {
                    rootComponent.navigateToLogin()
                } else {
                    rootComponent.navigateToSignUp()
                }
            }
            null -> { /* Still loading, do nothing */ }
        }
    }

    MaterialTheme {
        when (isSignedIn) {
            null -> LoadingScreen()
            else -> {
                // Always show the navigation stack once state is determined
                val childStack = rootComponent.childStack.subscribeAsState()
                Children(
                    stack = childStack.value,
                    animation = stackAnimation(slide())
                ) { child ->
                    when (val instance = child.instance) {
                        is RootComponent.Child.SignUpScreen -> SignUpScreen(instance.component)
                        is RootComponent.Child.LoginScreen -> LoginScreen(instance.component)
                        is RootComponent.Child.HomeScreen -> HomeScreen(instance.component)
                    }
                }
            }
        }
    }
}


@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
