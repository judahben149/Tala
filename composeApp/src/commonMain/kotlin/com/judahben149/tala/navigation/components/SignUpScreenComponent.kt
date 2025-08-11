package com.judahben149.tala.navigation.components

import com.arkivanov.decompose.ComponentContext

class SignUpScreenComponent(
    componentContext: ComponentContext,
    private val onNavigateToLogin: () -> Unit,
    private val onButtonClick: (String) -> Unit,
    private val onNavigateToHome: () -> Unit,
) : ComponentContext by componentContext {

    fun click() {
        onButtonClick("Hello from SignUpScreenComponent!")
    }

    fun navigateToLogin() {
        onNavigateToLogin()
    }

    fun navigateToHome() {
        onNavigateToHome()
    }
}