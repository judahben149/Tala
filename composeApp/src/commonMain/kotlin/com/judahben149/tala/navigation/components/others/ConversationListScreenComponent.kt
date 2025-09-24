package com.judahben149.tala.navigation.components.others

import com.arkivanov.decompose.ComponentContext

class ConversationListScreenComponent(
    componentContext: ComponentContext,
    private val onConversationSelected: (conversationId: String) -> Unit,
    private val onBackPressed: () -> Unit
) : ComponentContext by componentContext {

    fun conversationSelected(conversationId: String) {
        println("Conversation selected: $conversationId")
        onConversationSelected(conversationId)
    }

    fun goBack() {
        onBackPressed()
    }
}