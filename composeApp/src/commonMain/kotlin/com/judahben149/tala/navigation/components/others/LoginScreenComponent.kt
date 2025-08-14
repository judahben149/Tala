package com.judahben149.tala.navigation.components.others

import com.arkivanov.decompose.ComponentContext

class LoginScreenComponent(
    componentContext: ComponentContext,
    private val onNavigateToSignUp: () -> Unit,
    private val onLoginSuccess: () -> Unit,
    private val onBackPressed: () -> Unit
) : ComponentContext by componentContext {

    fun navigateToSignUp() {
        onNavigateToSignUp()
    }

    fun handleLoginSuccess() {
        onLoginSuccess()
    }

    fun goBack() {
        onBackPressed()
    }
}
