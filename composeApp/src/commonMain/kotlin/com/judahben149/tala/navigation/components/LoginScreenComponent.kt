package com.judahben149.tala.navigation.components

import com.arkivanov.decompose.ComponentContext

class LoginScreenComponent(
    componentContext: ComponentContext,
    private val text: String,
    private val onBackButtonClick: () -> Unit
) : ComponentContext by componentContext {

    fun getGreeting(): String = text

    fun goBack() {
        onBackButtonClick()
    }
}