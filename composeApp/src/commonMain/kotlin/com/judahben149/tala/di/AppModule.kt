package com.judahben149.tala.di

import com.judahben149.tala.data.local.AppDatabase
import com.judahben149.tala.data.local.DataStore
import com.judahben149.tala.data.local.dao.TestEntityDao
import com.judahben149.tala.data.repository.AuthRepositoryImpl
import com.judahben149.tala.data.repository.ChatRepositoryImpl
import com.judahben149.tala.data.repository.StorageRepositoryImpl
import com.judahben149.tala.domain.repository.AuthRepository
import com.judahben149.tala.domain.repository.ChatRepository
import com.judahben149.tala.domain.repository.StorageRepository
import com.judahben149.tala.domain.usecase.auth.SignInUseCase
import com.judahben149.tala.domain.usecase.chat.ChatUseCase
import com.judahben149.tala.domain.usecase.storage.StorageUseCase
import org.koin.dsl.module

val appModule = module {
    
    // DataStore
    single<DataStore> { createDataStore() }
    
    // Database
    single<AppDatabase> { createDatabase() }
    single<TestEntityDao> { get<AppDatabase>().testEntityDao() }
    
    // Repositories
    single<AuthRepository> { AuthRepositoryImpl() }
    single<ChatRepository> { ChatRepositoryImpl() }
    single<StorageRepository> { 
        StorageRepositoryImpl(
            testEntityDao = get(),
            dataStore = get()
        )
    }
    
    // Use Cases
    single { SignInUseCase(authRepository = get()) }
    single { ChatUseCase(chatRepository = get()) }
    single { StorageUseCase(storageRepository = get()) }
}
