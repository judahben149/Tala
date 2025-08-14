package com.judahben149.tala.navigation.components

import com.arkivanov.decompose.ComponentContext

class WelcomeScreenComponent(
    componentContext: ComponentContext,
    private val onContinue: () -> Unit
) : ComponentContext by componentContext {

    fun continueToNext() {
        onContinue()
    }
}