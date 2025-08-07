package com.judahben149.tala

import androidx.compose.runtime.*
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.componentContext
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stack
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.judahben149.tala.di.appModule
import com.judahben149.tala.presentation.navigation.RootComponent
import com.judahben149.tala.presentation.screen.*
import org.koin.compose.KoinContext
import org.koin.core.context.startKoin

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun App() {
    // Initialize Koin
    LaunchedEffect(Unit) {
        startKoin {
            modules(appModule)
        }
    }
    
    KoinContext {
        val root = remember {
            RootComponent(DefaultComponentContext(componentContext().lifecycle))
        }
        
        val childStack by root.childStack.subscribeAsState()
        
        Children(
            stack = childStack,
            animation = stack(fade()),
        ) { child ->
            when (val instance = child.instance) {
                is RootComponent.Child.Auth -> AuthScreen(component = instance.component)
                is RootComponent.Child.Chat -> ChatScreen(component = instance.component)
                is RootComponent.Child.RoomTest -> RoomTestScreen(component = instance.component)
                is RootComponent.Child.PrefsTest -> PrefsTestScreen(component = instance.component)
            }
        }
    }
}