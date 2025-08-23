package com.judahben149.tala.di

import co.touchlab.kermit.Logger
import com.judahben149.tala.data.local.ConversationDatabaseHelper
import com.judahben149.tala.data.local.UserDatabaseHelper
import com.judahben149.tala.util.preferences.PrefsPersister
import com.judahben149.tala.data.repository.AudioRepositoryImpl
import com.judahben149.tala.data.repository.AuthenticationRepositoryImpl
import com.judahben149.tala.data.repository.ConversationRepositoryImpl
import com.judahben149.tala.data.repository.GeminiRepositoryImpl
import com.judahben149.tala.data.repository.TtsRepositoryImpl
import com.judahben149.tala.data.repository.VoicesRepositoryImpl
import com.judahben149.tala.data.service.SignInStateTracker
import com.judahben149.tala.data.service.audio.SpeechRecorderFactory
import com.judahben149.tala.data.service.firebase.FirebaseService
import com.judahben149.tala.data.service.firebase.FirebaseServiceImpl
import com.judahben149.tala.data.service.gemini.GeminiService
import com.judahben149.tala.data.service.gemini.createGeminiService
import com.judahben149.tala.data.service.permission.AudioPermissionManager
import com.judahben149.tala.data.service.speechSynthesis.ElevenLabsService
import com.judahben149.tala.data.service.speechSynthesis.createElevenLabsService
import com.judahben149.tala.domain.managers.MessageManager
import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.domain.repository.AudioRepository
import com.judahben149.tala.domain.repository.AuthenticationRepository
import com.judahben149.tala.domain.repository.ConversationRepository
import com.judahben149.tala.domain.repository.ElevenLabsTtsRepository
import com.judahben149.tala.domain.repository.GeminiRepository
import com.judahben149.tala.domain.repository.VoicesRepository
import com.judahben149.tala.domain.usecases.analytics.GetLearningStatsUseCase
import com.judahben149.tala.domain.usecases.analytics.GetWeeklyProgressUseCase
import com.judahben149.tala.domain.usecases.analytics.TrackConversationTimeUseCase
import com.judahben149.tala.domain.usecases.analytics.UpdateConversationStatsUseCase
import com.judahben149.tala.domain.usecases.authentication.CreateUserUseCase
import com.judahben149.tala.domain.usecases.authentication.DeleteUserUseCase
import com.judahben149.tala.domain.usecases.authentication.GetCurrentAppUseCase
import com.judahben149.tala.domain.usecases.authentication.GetCurrentUserUseCase
import com.judahben149.tala.domain.usecases.authentication.IsUserSignedInUseCase
import com.judahben149.tala.domain.usecases.authentication.SendPasswordResetEmailUseCase
import com.judahben149.tala.domain.usecases.authentication.SignInUseCase
import com.judahben149.tala.domain.usecases.authentication.SignOutUseCase
import com.judahben149.tala.domain.usecases.authentication.UpdateDisplayNameUseCase
import com.judahben149.tala.domain.usecases.conversations.CompleteConversationUseCase
import com.judahben149.tala.domain.usecases.conversations.GetActiveConversationUseCase
import com.judahben149.tala.domain.usecases.conversations.GetConversationByIdUseCase
import com.judahben149.tala.domain.usecases.conversations.GetConversationHistoryUseCase
import com.judahben149.tala.domain.usecases.conversations.StartConversationUseCase
import com.judahben149.tala.domain.usecases.gemini.GenerateContentUseCase
import com.judahben149.tala.domain.usecases.messages.AddAiMessageUseCase
import com.judahben149.tala.domain.usecases.messages.AddUserMessageUseCase
import com.judahben149.tala.domain.usecases.messages.GetConversationMessagesUseCase
import com.judahben149.tala.domain.usecases.speech.ConvertSpeechToTextUseCase
import com.judahben149.tala.domain.usecases.speech.DownloadTextToSpeechUseCase
import com.judahben149.tala.domain.usecases.speech.GetAllVoicesUseCase
import com.judahben149.tala.domain.usecases.speech.GetFeaturedVoicesUseCase
import com.judahben149.tala.domain.usecases.speech.GetVoicesByGenderUseCase
import com.judahben149.tala.domain.usecases.speech.StreamTextToSpeechUseCase
import com.judahben149.tala.domain.usecases.speech.recording.CancelRecordingUseCase
import com.judahben149.tala.domain.usecases.speech.recording.ObserveRecordingStatusUseCase
import com.judahben149.tala.domain.usecases.speech.recording.StartRecordingUseCase
import com.judahben149.tala.domain.usecases.speech.recording.StopRecordingUseCase
import com.judahben149.tala.domain.usecases.vocabulary.AddVocabularyItemUseCase
import com.judahben149.tala.domain.usecases.vocabulary.GetRecentVocabularyUseCase
import com.judahben149.tala.domain.usecases.vocabulary.GetUserVocabularyUseCase
import com.judahben149.tala.presentation.screens.login.AuthViewModel
import com.judahben149.tala.presentation.screens.signUp.SignUpViewModel
import com.judahben149.tala.presentation.screens.speak.SpeakScreenViewModel
import com.judahben149.tala.util.ELEVEN_LABS_BASE_URL
import com.judahben149.tala.util.GEMINI_BASE_URL
import com.russhwolf.settings.Settings
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val appModule = module {
    // Services
    singleOf(::FirebaseServiceImpl).bind<FirebaseService>()
    singleOf<Settings>(::Settings)
    singleOf(::SignInStateTracker)
    single { Logger.withTag("Talaxx") }
    factory { get<SpeechRecorderFactory>().createRecorder() }
    singleOf(::AudioPermissionManager)
    single { Settings() }
    single { PrefsPersister(get()) }
    single { SessionManager(get(), get()) }
    singleOf(::MessageManager)

    // Network Clients
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                        encodeDefaults = true
                        explicitNulls = false
                    }
                )
            }

//            install(HttpTimeout) {
//                requestTimeoutMillis = 15_000
//                connectTimeoutMillis = 10_000
//                socketTimeoutMillis = 15_000
//            }


            install(Logging) {
                logger = io.ktor.client.plugins.logging.Logger.SIMPLE
                level = LogLevel.INFO
//                logger = object : io.ktor.client.plugins.logging.Logger {
//                    override fun log(message: String) {
//                        println("Request Header ->> $message")
//                    }
//                }
            }

        }
    }

//    single(named("ElevenLabsHttpClient")) {
//        HttpClient {
//            install(ContentNegotiation) {
//                json(
//                    Json {
//                        ignoreUnknownKeys = true
//                        isLenient = true
//                        encodeDefaults = true
//                        explicitNulls = false
//                    }
//                )
//            }
//            // Specific configurations for ElevenLabs API if needed
//        }
//    }

    single(named("GeminiKtorfit")) {
        Ktorfit.Builder()
            .baseUrl(GEMINI_BASE_URL)
            .httpClient(get<HttpClient>())
            .build()
    }

    single(named("ElevenLabsKtorfit")) {
        Ktorfit.Builder()
            .baseUrl(ELEVEN_LABS_BASE_URL)
            .httpClient(get<HttpClient>())
            .build()
    }
    single<GeminiService> { get<Ktorfit>(named("GeminiKtorfit")).createGeminiService() }
    single<ElevenLabsService> { get<Ktorfit>(named("ElevenLabsKtorfit")).createElevenLabsService() }

    // Repositories
    singleOf(::AuthenticationRepositoryImpl).bind<AuthenticationRepository>()
    singleOf(::GeminiRepositoryImpl).bind<GeminiRepository>()
    singleOf(::VoicesRepositoryImpl).bind<VoicesRepository>()
    singleOf(::TtsRepositoryImpl).bind<ElevenLabsTtsRepository>()
    singleOf(::AudioRepositoryImpl).bind<AudioRepository>()
    singleOf(::ConversationRepositoryImpl).bind<ConversationRepository>()


    // Use Cases
    singleOf(::SignInUseCase)
    singleOf(::CreateUserUseCase)
    singleOf(::GetCurrentUserUseCase)
    singleOf(::SignOutUseCase)
    singleOf(::UpdateDisplayNameUseCase)
    singleOf(::GetCurrentAppUseCase)
    singleOf(::IsUserSignedInUseCase)
    singleOf(::SendPasswordResetEmailUseCase)
    singleOf(::DeleteUserUseCase)
    singleOf(::GenerateContentUseCase)
    singleOf(::StreamTextToSpeechUseCase)
    singleOf(::DownloadTextToSpeechUseCase)
    singleOf(::GetVoicesByGenderUseCase)
    singleOf(::GetAllVoicesUseCase)
    singleOf(::GetFeaturedVoicesUseCase)
    singleOf(::StartRecordingUseCase)
    singleOf(::StopRecordingUseCase)
    singleOf(::CancelRecordingUseCase)
    singleOf(::ObserveRecordingStatusUseCase)
    singleOf(::ConvertSpeechToTextUseCase)
    singleOf(::StartConversationUseCase)
    singleOf(::GetActiveConversationUseCase)
    singleOf(::GetConversationHistoryUseCase)
    singleOf(::GetConversationByIdUseCase)
    singleOf(::CompleteConversationUseCase)
    singleOf(::AddAiMessageUseCase)
    singleOf(::AddUserMessageUseCase)
    singleOf(::GetConversationMessagesUseCase)
    singleOf(::AddVocabularyItemUseCase)
    singleOf(::GetRecentVocabularyUseCase)
    singleOf(::GetUserVocabularyUseCase)
    singleOf(::GetLearningStatsUseCase)
    singleOf(::GetWeeklyProgressUseCase)
    singleOf(::TrackConversationTimeUseCase)
    singleOf(::UpdateConversationStatsUseCase)


    // ViewModels
    viewModelOf(::SignUpViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::SpeakScreenViewModel)

    // Database
    single { UserDatabaseHelper(get()) }
    single { ConversationDatabaseHelper(get()) }

    // Platform-specific
    includes(platformModule)
}
