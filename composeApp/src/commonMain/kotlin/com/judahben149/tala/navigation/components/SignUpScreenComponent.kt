package com.judahben149.tala.navigation.components

import com.arkivanov.decompose.ComponentContext

class SignUpScreenComponent(
    componentContext: ComponentContext,
    private val onButtonClick: (String) -> Unit,
) : ComponentContext by componentContext {

    fun click() {
        onButtonClick("Hello from SignUpScreenComponent!")
    }
}