package com.judahben149.tala.navigation.components

import com.arkivanov.decompose.ComponentContext

class SignUpScreenComponent(
    componentContext: ComponentContext,
    private val onNavigateToLogin: () -> Unit,
    private val onSignUpSuccess: () -> Unit,
) : ComponentContext by componentContext {

    fun navigateToLogin() {
        onNavigateToLogin()
    }

    fun handleSignUpSuccess() {
        onSignUpSuccess()
    }
}