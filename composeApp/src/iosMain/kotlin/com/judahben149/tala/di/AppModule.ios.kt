package com.judahben149.tala.di

import com.judahben149.tala.data.local.DatabaseDriverFactory
import org.koin.dsl.module
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

fun initializeKoin() {
    try {
        stopKoin()
        startKoin {
            modules(appModule)
        }
    } catch (_: Exception) {
        startKoin {
            modules(appModule)
        }
    }
}

actual val platformModule = module {
    single<DatabaseDriverFactory> { DatabaseDriverFactory() }
}