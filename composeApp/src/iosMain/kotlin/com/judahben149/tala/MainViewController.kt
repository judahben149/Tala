package com.judahben149.tala

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.judahben149.tala.navigation.RootComponent

fun MainViewController() = ComposeUIViewController {

//    startKoin {
//        modules(appModule)
//    }

    val rootComponent = remember {
        RootComponent(
            componentContext = DefaultComponentContext(LifecycleRegistry())
        )
    }

    TalaApp(rootComponent)
}