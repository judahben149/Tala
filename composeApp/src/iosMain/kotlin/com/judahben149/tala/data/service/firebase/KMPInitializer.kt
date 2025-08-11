package com.judahben149.tala.data.service.firebase

import cocoapods.FirebaseCore.FIRApp
import kotlinx.cinterop.ExperimentalForeignApi

@OptIn(ExperimentalForeignApi::class)
fun onDidFinishLaunchingWithOptions() {
    println("KMP Initializer: Starting setup...")
    FIRApp.configure() // Call Firebase configure
    println("KMP Initializer: Firebase Configured.")
}