package com.judahben149.tala.data.service.firebase

import cocoapods.FirebaseCore.FIRApp
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.context.startKoin

@OptIn(ExperimentalForeignApi::class)
fun onDidFinishLaunchingWithOptions() {
    println("KMP Initializer: Starting setup...")
    FIRApp.configure() // Call Firebase configure
    println("KMP Initializer: Firebase Configured.")
}

private fun initializeKoin() {
    if (getKoinApplicationOrNull() == null) {
        startKoin {
            // your modules here
            modules(yourAppModule)
        }
    }
}