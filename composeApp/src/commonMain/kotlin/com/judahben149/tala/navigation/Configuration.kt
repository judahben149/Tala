package com.judahben149.tala.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Configuration {
    @Serializable
    data object SignUpScreen : Configuration()

    @Serializable
    data class LoginScreen(val text: String) : Configuration()
}