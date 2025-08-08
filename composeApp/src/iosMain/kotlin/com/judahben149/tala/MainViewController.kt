package com.judahben149.tala

import androidx.compose.ui.window.ComposeUIViewController
import com.judahben149.tala.di.appModule
import com.judahben149.tala.di.iosAppModule
import org.koin.core.context.startKoin

fun MainViewController() = ComposeUIViewController {

    startKoin {
        modules(appModule + iosAppModule)
    }
    TalaApp()
}