package com.judahben149.tala.di

import android.app.Activity
import com.judahben149.tala.data.local.DatabaseDriverFactory
import com.judahben149.tala.data.service.audio.AndroidAudioRecorderFactory
import com.judahben149.tala.data.service.audio.AudioPlayerFactory
import com.judahben149.tala.data.service.audio.SpeechRecorderFactory
import com.judahben149.tala.domain.usecases.permissions.CheckRecordingPermissionUseCase
import com.judahben149.tala.domain.usecases.permissions.RequestRecordingPermissionUseCase
import com.judahben149.tala.util.AudioFileManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual val platformModule = module {
    single<DatabaseDriverFactory> { DatabaseDriverFactory(androidContext()) }
    single { AudioPlayerFactory().create(androidContext(), get()) }
    single<SpeechRecorderFactory> { AndroidAudioRecorderFactory() }
    single<AudioFileManager> { AudioFileManager(androidContext()) }

    // Platform UseCases
    single { CheckRecordingPermissionUseCase(androidContext()) }
    single { RequestRecordingPermissionUseCase(androidContext() as Activity) }
}