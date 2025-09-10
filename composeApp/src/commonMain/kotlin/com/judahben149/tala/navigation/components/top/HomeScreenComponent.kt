package com.judahben149.tala.navigation.components.top

import com.arkivanov.decompose.ComponentContext

class HomeScreenComponent(
    componentContext: ComponentContext,
    private val onNavigateToProfile: () -> Unit,
//    private val onNavigateToSpeak: () -> Unit,
    private val onNavigateToVoices: () -> Unit,
    private val onNavigateToSettings: () -> Unit,
    private val onNavigateToSpeakingModeSelection: () -> Unit,
) : ComponentContext by componentContext {

    fun navigateToProfile() {
        onNavigateToProfile()
    }

    fun navigateToSpeakingModeSelection() {
        onNavigateToSpeakingModeSelection()
    }

//    fun navigateToSpeak() {
//        onNavigateToSpeak()
//    }

    fun navigateToVoices() {
        onNavigateToVoices()
    }

    fun navigateToSettings() {
        onNavigateToSettings()
    }
}