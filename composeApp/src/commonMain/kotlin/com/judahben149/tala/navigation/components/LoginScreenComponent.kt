package com.judahben149.tala.navigation.components

import com.arkivanov.decompose.ComponentContext

class LoginScreenComponent(
    componentContext: ComponentContext,
    private val onBackButtonClick: () -> Unit,
    private val onNavigateToHome: () -> Unit,
    private val onNavigateToSignUp: () -> Unit
) : ComponentContext by componentContext {

    fun goBack() {
        onBackButtonClick()
    }

    fun navigateToHome() {
        onNavigateToHome()
    }

    fun navigateToSignUp() {
        onNavigateToSignUp()
    }
}