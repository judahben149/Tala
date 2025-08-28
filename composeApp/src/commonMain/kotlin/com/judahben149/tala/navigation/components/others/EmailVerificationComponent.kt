package com.judahben149.tala.navigation.components.others

import com.arkivanov.decompose.ComponentContext

class EmailVerificationComponent(
    componentContext: ComponentContext,
    val userEmail: String,
    private val onNavigateToWelcome: () -> Unit,
    private val onBackPressed: () -> Unit
) : ComponentContext by componentContext {

    fun navigateToWelcome() {
        onNavigateToWelcome()
    }

    fun navigateBack() {
        onBackPressed()
    }
}
