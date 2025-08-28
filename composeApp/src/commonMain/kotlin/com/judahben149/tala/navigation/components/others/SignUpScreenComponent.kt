package com.judahben149.tala.navigation.components.others

import com.arkivanov.decompose.ComponentContext

class SignUpScreenComponent(
    componentContext: ComponentContext,
    private val onNavigateToLogin: () -> Unit,
    private val onNavigateToEmailVerification: (email: String) -> Unit,
) : ComponentContext by componentContext {

    fun navigateToLogin() {
        onNavigateToLogin()
    }

    fun navigateToEmailVerification(email: String) {
        onNavigateToEmailVerification(email)
    }
}