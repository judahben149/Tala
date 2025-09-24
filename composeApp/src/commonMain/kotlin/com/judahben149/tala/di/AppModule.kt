package com.judahben149.tala.di

import co.touchlab.kermit.Logger
import com.judahben149.tala.data.local.ConversationDatabaseHelper
import com.judahben149.tala.data.local.UserDatabaseHelper
import com.judahben149.tala.data.local.VoicesDatabaseHelper
import com.judahben149.tala.data.repository.AudioRepositoryImpl
import com.judahben149.tala.data.repository.AuthenticationRepositoryImpl
import com.judahben149.tala.data.repository.ConversationRepositoryImpl
import com.judahben149.tala.data.repository.GeminiRepositoryImpl
import com.judahben149.tala.data.repository.TtsRepositoryImpl
import com.judahben149.tala.data.repository.UserRepositoryImpl
import com.judahben149.tala.data.repository.VoicesRepositoryImpl
import com.judahben149.tala.data.service.audio.SpeechRecorderFactory
import com.judahben149.tala.data.service.firebase.FirebaseService
import com.judahben149.tala.data.service.firebase.FirebaseServiceImpl
import com.judahben149.tala.data.service.gemini.GeminiService
import com.judahben149.tala.data.service.gemini.createGeminiService
import com.judahben149.tala.data.service.permission.AudioPermissionManager
import com.judahben149.tala.data.service.speechSynthesis.ElevenLabsService
import com.judahben149.tala.data.service.speechSynthesis.createElevenLabsService
import com.judahben149.tala.domain.managers.AdvancedPromptBuilder
import com.judahben149.tala.domain.managers.FirebaseSyncManager
import com.judahben149.tala.domain.managers.MessageManager
import com.judahben149.tala.domain.managers.RemoteConfigManager
import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.domain.repository.AudioRepository
import com.judahben149.tala.domain.repository.AuthenticationRepository
import com.judahben149.tala.domain.repository.ConversationRepository
import com.judahben149.tala.domain.repository.ElevenLabsTtsRepository
import com.judahben149.tala.domain.repository.GeminiRepository
import com.judahben149.tala.domain.repository.UserRepository
import com.judahben149.tala.domain.repository.VoicesRepository
import com.judahben149.tala.domain.usecases.analytics.GetLearningStatsUseCase
import com.judahben149.tala.domain.usecases.analytics.GetWeeklyProgressUseCase
import com.judahben149.tala.domain.usecases.analytics.TrackConversationTimeUseCase
import com.judahben149.tala.domain.usecases.analytics.UpdateConversationStatsUseCase
import com.judahben149.tala.domain.usecases.authentication.CreateDefaultUserDataUseCase
import com.judahben149.tala.domain.usecases.authentication.CreateUserUseCase
import com.judahben149.tala.domain.usecases.authentication.DeleteAccountWithAuthUseCase
import com.judahben149.tala.domain.usecases.authentication.GetCurrentAppUseCase
import com.judahben149.tala.domain.usecases.authentication.GetCurrentUserUseCase
import com.judahben149.tala.domain.usecases.authentication.GetUserDataUseCase
import com.judahben149.tala.domain.usecases.authentication.IsUserSignedInUseCase
import com.judahben149.tala.domain.usecases.authentication.RefreshUserTokenUseCase
import com.judahben149.tala.domain.usecases.authentication.SendPasswordResetEmailUseCase
import com.judahben149.tala.domain.usecases.authentication.SignInUseCase
import com.judahben149.tala.domain.usecases.authentication.SignOutUseCase
import com.judahben149.tala.domain.usecases.authentication.UpdateDisplayNameUseCase
import com.judahben149.tala.domain.usecases.authentication.verification.CheckEmailVerificationUseCase
import com.judahben149.tala.domain.usecases.authentication.verification.SendEmailVerificationUseCase
import com.judahben149.tala.domain.usecases.conversations.CompleteConversationUseCase
import com.judahben149.tala.domain.usecases.conversations.GetActiveConversationUseCase
import com.judahben149.tala.domain.usecases.conversations.GetAudioFileUseCase
import com.judahben149.tala.domain.usecases.conversations.GetConversationByIdUseCase
import com.judahben149.tala.domain.usecases.conversations.GetConversationHistoryUseCase
import com.judahben149.tala.domain.usecases.conversations.GetMasteryLevelUseCase
import com.judahben149.tala.domain.usecases.conversations.IncrementConversationCountUseCase
import com.judahben149.tala.domain.usecases.conversations.StartConversationUseCase
import com.judahben149.tala.domain.usecases.gemini.GenerateContentUseCase
import com.judahben149.tala.domain.usecases.messages.AddAiMessageUseCase
import com.judahben149.tala.domain.usecases.messages.AddUserMessageUseCase
import com.judahben149.tala.domain.usecases.messages.GetConversationMessagesUseCase
import com.judahben149.tala.domain.usecases.preferences.GetSavedUserInterestsUseCase
import com.judahben149.tala.domain.usecases.preferences.SaveLearningLanguageUseCase
import com.judahben149.tala.domain.usecases.preferences.SaveUserInterestsUseCase
import com.judahben149.tala.domain.usecases.settings.GetLearningLanguageUseCase
import com.judahben149.tala.domain.usecases.settings.GetNotificationSettingsUseCase
import com.judahben149.tala.domain.usecases.settings.GetUserProfileUseCase
import com.judahben149.tala.domain.usecases.settings.UpdateLearningLanguageUseCase
import com.judahben149.tala.domain.usecases.settings.UpdateNotificationSettingsUseCase
import com.judahben149.tala.domain.usecases.settings.UpdatePasswordUseCase
import com.judahben149.tala.domain.usecases.settings.UpdateUserProfileUseCase
import com.judahben149.tala.domain.usecases.speech.ConvertSpeechToTextUseCase
import com.judahben149.tala.domain.usecases.speech.DownloadTextToSpeechUseCase
import com.judahben149.tala.domain.usecases.speech.GetAllVoicesUseCase
import com.judahben149.tala.domain.usecases.speech.GetFeaturedVoicesUseCase
import com.judahben149.tala.domain.usecases.speech.GetSelectedVoiceIdUseCase
import com.judahben149.tala.domain.usecases.speech.GetSelectedVoiceUseCase
import com.judahben149.tala.domain.usecases.speech.GetVoicesByGenderUseCase
import com.judahben149.tala.domain.usecases.speech.ObserveSelectedVoiceUseCase
import com.judahben149.tala.domain.usecases.speech.SaveSelectedVoiceUseCase
import com.judahben149.tala.domain.usecases.speech.SetVoiceSelectionCompleteUseCase
import com.judahben149.tala.domain.usecases.speech.StreamTextToSpeechUseCase
import com.judahben149.tala.domain.usecases.speech.recording.CancelRecordingUseCase
import com.judahben149.tala.domain.usecases.speech.recording.ObserveAudioLevelsUseCase
import com.judahben149.tala.domain.usecases.speech.recording.ObserveRecordingStatusUseCase
import com.judahben149.tala.domain.usecases.speech.recording.StartRecordingUseCase
import com.judahben149.tala.domain.usecases.speech.recording.StopRecordingUseCase
import com.judahben149.tala.domain.usecases.user.ClearPersistedUserUseCase
import com.judahben149.tala.domain.usecases.user.ObservePersistedUserDataUseCase
import com.judahben149.tala.domain.usecases.user.PersistUserDataUseCase
import com.judahben149.tala.domain.usecases.user.UpdateOnboardingFlagUseCase
import com.judahben149.tala.domain.usecases.vocabulary.AddVocabularyItemUseCase
import com.judahben149.tala.domain.usecases.vocabulary.GetRecentVocabularyUseCase
import com.judahben149.tala.domain.usecases.vocabulary.GetUserVocabularyUseCase
import com.judahben149.tala.presentation.screens.history.ConversationHistoryViewModel
import com.judahben149.tala.presentation.screens.history.detail.ConversationDetailViewModel
import com.judahben149.tala.presentation.screens.home.HomeScreenViewModel
import com.judahben149.tala.presentation.screens.login.AuthViewModel
import com.judahben149.tala.presentation.screens.settings.SettingsScreenViewModel
import com.judahben149.tala.presentation.screens.signUp.SignUpViewModel
import com.judahben149.tala.presentation.screens.signUp.interests.InterestsSelectionViewModel
import com.judahben149.tala.presentation.screens.signUp.language.LanguageSelectionViewModel
import com.judahben149.tala.presentation.screens.signUp.verification.EmailVerificationViewModel
import com.judahben149.tala.presentation.screens.signUp.welcome.WelcomeScreenViewModel
import com.judahben149.tala.presentation.screens.speak.SpeakScreenViewModel
import com.judahben149.tala.presentation.screens.speak.guidedPractice.GuidedPracticeViewModel
import com.judahben149.tala.presentation.screens.speak.speakingModeSelection.SpeakingModeSelectionViewModel
import com.judahben149.tala.presentation.screens.voices.VoicesScreenViewModel
import com.judahben149.tala.util.ELEVEN_LABS_BASE_URL
import com.judahben149.tala.util.GEMINI_BASE_URL
import com.judahben149.tala.util.preferences.PrefsPersister
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
    single { Logger.withTag("Talaxx") }
    factory { get<SpeechRecorderFactory>().createRecorder() }
    singleOf(::AudioPermissionManager)
    single { Settings() }
    single { PrefsPersister(get()) }
    single { SessionManager(get(), get()) }
    singleOf(::MessageManager)
    singleOf(::AdvancedPromptBuilder)

    single {
        FirebaseSyncManager(
            firebaseService = get(),
            persistUserDataUseCase = get(),
            observePersistedUserDataUseCase = get(),
            sessionManager = get(),
            logger = get()
        )
    }

    single<RemoteConfigManager> {
        RemoteConfigManager(
            firebaseService = get(),
            logger = get()
        )
    }

    // Network Clients
    single(named("DefaultHttpClient")) {
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

    single(named("ElevenLabsHttpClient")) {
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
            // Specific configurations for ElevenLabs API if needed
            // For instance, if ElevenLabs requires different logging or timeout settings.
        }
    }

    single(named("GeminiKtorfit")) {
        Ktorfit.Builder()
            .baseUrl(GEMINI_BASE_URL)
            .httpClient(get<HttpClient>(named("DefaultHttpClient")))
            .build()
    }

    single(named("ElevenLabsKtorfit")) {
        Ktorfit.Builder()
            .baseUrl(ELEVEN_LABS_BASE_URL)
            .httpClient(get<HttpClient>(named("ElevenLabsHttpClient"))) // Use the specific client for ElevenLabs
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
    singleOf(::UserRepositoryImpl).bind<UserRepository>()


    // Use Cases
    singleOf(::SignInUseCase)
    singleOf(::CreateUserUseCase)
    singleOf(::GetCurrentUserUseCase)
    singleOf(::SignOutUseCase)
    singleOf(::UpdateDisplayNameUseCase)
    singleOf(::GetCurrentAppUseCase)
    singleOf(::IsUserSignedInUseCase)
    singleOf(::SendPasswordResetEmailUseCase)
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
    singleOf(::GetSelectedVoiceUseCase)
    singleOf(::SaveSelectedVoiceUseCase)
    singleOf(::SetVoiceSelectionCompleteUseCase)
    singleOf(::GetUserProfileUseCase)
    singleOf(::UpdateUserProfileUseCase)
    singleOf(::UpdatePasswordUseCase)
    singleOf(::UpdateLearningLanguageUseCase)
    singleOf(::GetLearningLanguageUseCase)
    singleOf(::UpdateNotificationSettingsUseCase)
    singleOf(::SendEmailVerificationUseCase)
    singleOf(::CheckEmailVerificationUseCase)
    singleOf(::GetSelectedVoiceIdUseCase)
    singleOf(::DeleteAccountWithAuthUseCase)
    singleOf(::RefreshUserTokenUseCase)
    singleOf(::SaveLearningLanguageUseCase)
    singleOf(::SaveUserInterestsUseCase)
    singleOf(::GetNotificationSettingsUseCase)
    singleOf(::GetUserDataUseCase)
    singleOf(::CreateDefaultUserDataUseCase)
    singleOf(::PersistUserDataUseCase)
    singleOf(::ObservePersistedUserDataUseCase)
    singleOf(::UpdateOnboardingFlagUseCase)
    singleOf(::ClearPersistedUserUseCase)
    singleOf(::ObserveSelectedVoiceUseCase)
    singleOf(::GetSavedUserInterestsUseCase)
    singleOf(::ObserveAudioLevelsUseCase)
    singleOf(::GetMasteryLevelUseCase)
    singleOf(::IncrementConversationCountUseCase)
    singleOf(::GetAudioFileUseCase)


    // ViewModels
    viewModelOf(::SignUpViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::SpeakScreenViewModel)
    viewModelOf(::HomeScreenViewModel)
    viewModelOf(::VoicesScreenViewModel)
    viewModelOf(::SettingsScreenViewModel)
    viewModelOf(::EmailVerificationViewModel)
    viewModelOf(::LanguageSelectionViewModel)
    viewModelOf(::InterestsSelectionViewModel)
    viewModelOf(::WelcomeScreenViewModel)
    viewModelOf(::SpeakingModeSelectionViewModel)
    viewModelOf(::GuidedPracticeViewModel)
    viewModelOf(::ConversationHistoryViewModel)
    viewModelOf(::ConversationDetailViewModel)

    // Database
    single { UserDatabaseHelper(get(), get()) }
    single { ConversationDatabaseHelper(get()) }
    single { VoicesDatabaseHelper(get()) }

    // Platform-specific
    includes(platformModule)
}
