package com.judahben149.tala

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.judahben149.tala.data.service.SignInStateTracker
import com.judahben149.tala.navigation.RootComponent
import org.koin.compose.koinInject

fun MainViewController() = ComposeUIViewController {

    val signInStateTracker: SignInStateTracker = koinInject()

    val rootComponent = remember {
        RootComponent(
            componentContext = DefaultComponentContext(LifecycleRegistry()),
            signInStateTracker
        )
    }

    TalaApp(rootComponent)
}