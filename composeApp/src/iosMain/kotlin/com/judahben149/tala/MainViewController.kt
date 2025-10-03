package com.judahben149.tala

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
//import com.judahben149.tala.core.purchases.initRevenueCat
import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.navigation.RootComponent
import org.koin.compose.koinInject

fun MainViewController() = ComposeUIViewController {

    val sessionManager: SessionManager = koinInject()


    val rootComponent = remember {
        RootComponent(
            componentContext = DefaultComponentContext(LifecycleRegistry()),
            sessionManager
        )
    }

//    initRevenueCat()
    TalaApp(rootComponent)
}