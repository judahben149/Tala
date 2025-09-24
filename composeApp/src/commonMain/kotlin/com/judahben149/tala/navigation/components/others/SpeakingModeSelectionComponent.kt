package com.judahben149.tala.navigation.components.others

import com.arkivanov.decompose.ComponentContext
import com.judahben149.tala.domain.models.conversation.SpeakingMode

class SpeakingModeSelectionComponent(
    componentContext: ComponentContext,
    private val onFreeSpeak: () -> Unit,
    private val onGuidedPractice: () -> Unit,
    private val onBackPressed: () -> Unit
) : ComponentContext by componentContext {

    fun onModeSelected(mode: SpeakingMode) {
        when (mode) {
            SpeakingMode.FREE_SPEAK -> onFreeSpeak()
            SpeakingMode.GUIDED_PRACTICE -> onGuidedPractice()
        }
    }

    fun goBack() {
        onBackPressed()
    }
}