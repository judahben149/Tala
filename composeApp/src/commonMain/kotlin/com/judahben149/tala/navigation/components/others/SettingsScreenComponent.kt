package com.judahben149.tala.navigation.components.others

import com.arkivanov.decompose.ComponentContext

class SettingsScreenComponent(
    componentContext: ComponentContext,
    private val onNavigateToTerms: () -> Unit,
    private val onNavigateToSupport: () -> Unit,
    private val onNavigateToPrivacyPolicy: () -> Unit,
    private val onNavigateToFeedback: () -> Unit,
    private val onBackPressed: () -> Unit
) : ComponentContext by componentContext {

    fun navigateToTerms() {
        onNavigateToTerms()
    }

    fun navigateToSupport() {
        onNavigateToSupport()
    }

    fun navigateToPrivacyPolicy() {
        onNavigateToPrivacyPolicy()
    }

    fun navigateToFeedback() {
        onNavigateToFeedback()
    }

    fun goBack() {
        onBackPressed()
    }
}