package com.judahben149.tala.di

import com.judahben149.tala.data.TalaDatabase
import com.judahben149.tala.data.local.getTalaDatabaseBuilder
import org.koin.dsl.module
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

fun initializeKoin() {
    try {
        stopKoin()
        startKoin {
            modules(appModule + iosAppModule)
        }
    } catch (e: Exception) {
        startKoin {
            modules(appModule + iosAppModule)
        }
    }
}

val iosAppModule = module {
    single<TalaDatabase> { getTalaDatabaseBuilder() }
}