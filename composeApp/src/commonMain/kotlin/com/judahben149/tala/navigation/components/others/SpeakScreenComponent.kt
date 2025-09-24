package com.judahben149.tala.navigation.components.others

import com.arkivanov.decompose.ComponentContext
import com.judahben149.tala.domain.models.conversation.GuidedPracticeScenario
import com.judahben149.tala.domain.models.conversation.SpeakingMode

class SpeakScreenComponent(
    componentContext: ComponentContext,
    val speakingMode: SpeakingMode = SpeakingMode.FREE_SPEAK,
    val scenario: GuidedPracticeScenario? = null,
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