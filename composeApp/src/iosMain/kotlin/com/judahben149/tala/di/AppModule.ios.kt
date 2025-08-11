package com.judahben149.tala.di

import com.judahben149.tala.data.TalaDatabase
import com.judahben149.tala.data.local.getTalaDatabaseBuilder
import org.koin.dsl.module

fun initializeKoin() {
        startKoin {
            modules(appModule + iosAppModule)
        }
}

val iosAppModule = module {
    single<TalaDatabase> { getTalaDatabaseBuilder() }
}