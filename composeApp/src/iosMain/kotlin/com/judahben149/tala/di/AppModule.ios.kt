package com.judahben149.tala.di

import com.judahben149.tala.data.local.DatabaseDriverFactory
import com.judahben149.tala.data.service.audio.AudioPlayerFactory
import com.judahben149.tala.data.service.audio.IOSAudioRecorderFactory
import com.judahben149.tala.data.service.audio.SpeechRecorderFactory
import com.judahben149.tala.domain.usecases.permissions.CheckRecordingPermissionUseCase
import com.judahben149.tala.domain.usecases.permissions.RequestRecordingPermissionUseCase
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
    single { AudioPlayerFactory().create(get()) }
    single<SpeechRecorderFactory> { IOSAudioRecorderFactory() }

    // Platform UseCases
    single { CheckRecordingPermissionUseCase() }
    single { RequestRecordingPermissionUseCase() }
}