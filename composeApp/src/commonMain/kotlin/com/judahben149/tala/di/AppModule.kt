package com.judahben149.tala.di

import co.touchlab.kermit.Logger
import com.judahben149.tala.data.local.UserDatabaseHelper
import com.judahben149.tala.data.service.firebase.FirebaseService
import com.judahben149.tala.data.service.firebase.FirebaseServiceImpl
import com.judahben149.tala.domain.repository.AuthenticationRepository
import com.judahben149.tala.data.repository.AuthenticationRepositoryImpl
import com.judahben149.tala.data.repository.GeminiRepositoryImpl
import com.judahben149.tala.data.service.SignInStateTracker
import com.judahben149.tala.data.service.gemini.GeminiService
import com.judahben149.tala.data.service.gemini.createGeminiService
import com.judahben149.tala.domain.repository.GeminiRepository
import com.judahben149.tala.domain.usecases.authentication.CreateUserUseCase
import com.judahben149.tala.domain.usecases.authentication.DeleteUserUseCase
import com.judahben149.tala.domain.usecases.authentication.GetCurrentAppUseCase
import com.judahben149.tala.domain.usecases.authentication.GetCurrentUserUseCase
import com.judahben149.tala.domain.usecases.authentication.IsUserSignedInUseCase
import com.judahben149.tala.domain.usecases.authentication.SendPasswordResetEmailUseCase
import com.judahben149.tala.domain.usecases.authentication.SignInUseCase
import com.judahben149.tala.domain.usecases.authentication.SignOutUseCase
import com.judahben149.tala.domain.usecases.authentication.UpdateDisplayNameUseCase
import com.judahben149.tala.domain.usecases.gemini.GenerateContentUseCase
import com.judahben149.tala.presentation.screens.login.AuthViewModel
import com.judahben149.tala.presentation.screens.signUp.SignUpViewModel
import com.judahben149.tala.presentation.screens.speak.SpeakScreenViewModel
import com.judahben149.tala.util.GEMINI_BASE_URL
import com.russhwolf.settings.Settings
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val appModule = module {
    // Services
    singleOf(::FirebaseServiceImpl).bind<FirebaseService>()
    singleOf<Settings>(::Settings)
    singleOf(::SignInStateTracker)
    single { Logger.withTag("Talaxx") }

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
            // Optional: timeouts, logging, default headers, etc.

        }
    }

    single {
        Ktorfit.Builder()
            .baseUrl(GEMINI_BASE_URL)
            .httpClient(get<HttpClient>())
            .build()
    }

    single<GeminiService> { get<Ktorfit>().createGeminiService() }

    // Repositories
    singleOf(::AuthenticationRepositoryImpl).bind<AuthenticationRepository>()
    singleOf(::GeminiRepositoryImpl).bind<GeminiRepository>()


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

    // ViewModels
    viewModelOf(::SignUpViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::SpeakScreenViewModel)

    // Database
    single { UserDatabaseHelper(get()) }

    // Platform-specific
    includes(platformModule)
}
