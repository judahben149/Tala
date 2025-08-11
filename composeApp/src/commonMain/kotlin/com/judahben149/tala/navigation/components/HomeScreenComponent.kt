package com.judahben149.tala.navigation.components

import com.arkivanov.decompose.ComponentContext

class HomeScreenComponent(
    componentContext: ComponentContext,
    private val onBackButtonClick: () -> Unit
) : ComponentContext by componentContext {

    fun goBack() {
        onBackButtonClick()
    }
}