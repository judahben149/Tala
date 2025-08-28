package com.judahben149.tala.navigation.components.others

import com.arkivanov.decompose.ComponentContext
import com.judahben149.tala.domain.managers.SessionManager

class EmailVerificationComponent(
    componentContext: ComponentContext,
    val userEmail: String,
    private val onNavigateToWelcome: () -> Unit,
    private val onBackPressed: () -> Unit,
    private val sessionManager: SessionManager,
) : ComponentContext by componentContext {

    fun onEmailVerified(userId: String) {
        sessionManager.markSignedIn(
            userId = userId,
            isNewUser = true
        )
        onNavigateToWelcome()
    }

    fun navigateBack() {
        onBackPressed()
    }
}
