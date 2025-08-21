package com.judahben149.tala.navigation.components.others

import com.arkivanov.decompose.ComponentContext

class SpeakScreenComponent(
    componentContext: ComponentContext,
    private val onViewConversationList: () -> Unit,
    private val onBackPressed: () -> Unit
) : ComponentContext by componentContext {

    fun viewConversationList() {
        onViewConversationList()
    }

    fun goBack() {
        onBackPressed()
    }
}