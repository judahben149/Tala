package com.judahben149.tala.di

import com.judahben149.tala.data.local.DatabaseDriverFactory
import com.judahben149.tala.data.service.audio.AudioPlayerFactory
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseDriverFactory> { DatabaseDriverFactory(androidContext()) }
    single { AudioPlayerFactory().create(androidContext(), get()) }
}