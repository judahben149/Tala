package com.judahben149.tala.data.service.firebase

import cocoapods.FirebaseCore.FIRApp
import com.judahben149.tala.di.initializeKoin
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
fun onDidFinishLaunchingWithOptions() {
    println("KMP Initializer: Starting setup...")
    FIRApp.configure() // Call Firebase configure
    initKoin()
    println("KMP Initializer: Firebase Configured.")
}

private fun initKoin() {
    initializeKoin()
}