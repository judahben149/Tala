package com.judahben149.tala.navigation.components.top

import com.arkivanov.decompose.ComponentContext

class HomeScreenComponent(
    componentContext: ComponentContext,
    private val onNavigateToProfile: () -> Unit,
    private val onNavigateToSpeak: () -> Unit,
) : ComponentContext by componentContext {

    fun navigateToProfile() {
        onNavigateToProfile()
    }

    fun navigateToSpeak() {
        onNavigateToSpeak()
    }
}