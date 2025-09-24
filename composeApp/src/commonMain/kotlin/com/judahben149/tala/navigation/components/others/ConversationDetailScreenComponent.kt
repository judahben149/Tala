package com.judahben149.tala.navigation.components.others

import com.arkivanov.decompose.ComponentContext
import com.judahben149.tala.domain.models.conversation.GuidedPracticeScenario
import com.judahben149.tala.domain.models.conversation.SpeakingMode

class ConversationDetailScreenComponent(
    componentContext: ComponentContext,
    val conversationId: String? = null,
    private val onBackPressed: () -> Unit
) : ComponentContext by componentContext {

    fun goBack() {
        onBackPressed()
    }
}