package com.judahben149.tala.navigation.components.others

import com.arkivanov.decompose.ComponentContext
import com.judahben149.tala.domain.models.conversation.GuidedPracticeScenario

class GuidedPracticeScreenComponent(
    componentContext: ComponentContext,
    private val onBeginSpeech: (scenario: GuidedPracticeScenario) -> Unit,
    private val onBackPressed: () -> Unit
) : ComponentContext by componentContext {

    fun scenarioSelected(
        scenario: GuidedPracticeScenario
    ) {
        onBeginSpeech(scenario)
    }

    fun goBack() {
        onBackPressed()
    }
}