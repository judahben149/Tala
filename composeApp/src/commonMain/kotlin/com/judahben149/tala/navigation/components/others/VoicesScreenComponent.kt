package com.judahben149.tala.navigation.components.others

import com.arkivanov.decompose.ComponentContext

class VoicesScreenComponent(
    componentContext: ComponentContext,
    private val onVoiceSelected: () -> Unit,
    private val onBackPressed: () -> Unit
) : ComponentContext by componentContext {

    fun voiceSelected() {
        onVoiceSelected()
    }

    fun goBack() {
        onBackPressed()
    }
}