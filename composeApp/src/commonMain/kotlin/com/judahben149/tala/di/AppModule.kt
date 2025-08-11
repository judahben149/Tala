package com.judahben149.tala.di

import com.judahben149.tala.data.service.firebase.FirebaseService
import com.judahben149.tala.data.service.firebase.FirebaseServiceImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    singleOf(::FirebaseServiceImpl).bind<FirebaseService>()
}