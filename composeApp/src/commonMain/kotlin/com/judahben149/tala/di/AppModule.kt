package com.judahben149.tala.di

import com.judahben149.tala.data.local.UserDatabaseHelper
import com.judahben149.tala.data.service.firebase.FirebaseService
import com.judahben149.tala.data.service.firebase.FirebaseServiceImpl
import com.judahben149.tala.domain.repository.AuthenticationRepository
import com.judahben149.tala.data.repository.AuthenticationRepositoryImpl
import com.judahben149.tala.domain.usecases.authentication.CreateUserUseCase
import com.judahben149.tala.domain.usecases.authentication.GetCurrentAppUseCase
import com.judahben149.tala.domain.usecases.authentication.GetCurrentUserUseCase
import com.judahben149.tala.domain.usecases.authentication.IsUserSignedInUseCase
import com.judahben149.tala.domain.usecases.authentication.SignInUseCase
import com.judahben149.tala.domain.usecases.authentication.SignOutUseCase
import com.judahben149.tala.domain.usecases.authentication.UpdateDisplayNameUseCase
import com.judahben149.tala.presentation.screens.login.AuthViewModel
import com.judahben149.tala.presentation.screens.signUp.SignUpViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val appModule = module {
    // Services
    singleOf(::FirebaseServiceImpl).bind<FirebaseService>()

    // Repositories
    singleOf(::AuthenticationRepositoryImpl).bind<AuthenticationRepository>()

    // Use Cases
    singleOf(::SignInUseCase)
    singleOf(::CreateUserUseCase)
    singleOf(::GetCurrentUserUseCase)
    singleOf(::SignOutUseCase)
    singleOf(::UpdateDisplayNameUseCase)
    singleOf(::GetCurrentAppUseCase)
    singleOf(::IsUserSignedInUseCase)

    // ViewModels
    viewModelOf(::SignUpViewModel)
    viewModelOf(::AuthViewModel)

    // Database
    single { UserDatabaseHelper(get()) }

    // Platform-specific
    includes(platformModule)
}
