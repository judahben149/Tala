package com.judahben149.tala.navigation.flow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.judahben149.tala.navigation.MainChild
import com.judahben149.tala.navigation.MainFlowComponent
import com.judahben149.tala.presentation.screens.history.ConversationListScreen
import com.judahben149.tala.presentation.screens.history.detail.ConversationDetailScreen
import com.judahben149.tala.presentation.screens.home.HomeScreen
import com.judahben149.tala.presentation.screens.settings.SettingsScreen
import com.judahben149.tala.presentation.screens.speak.SpeakScreen
import com.judahben149.tala.presentation.screens.speak.guidedPractice.GuidedPracticeScreen
import com.judahben149.tala.presentation.screens.speak.speakingModeSelection.SpeakingModeSelectionScreen
import com.judahben149.tala.presentation.screens.voices.VoicesScreen

@Composable
fun MainFlow(component: MainFlowComponent) {
    val childStack by component.childStack.subscribeAsState()
    
    Children(
        stack = childStack,
        animation = stackAnimation(slide())
    ) { child ->
        when (val instance = child.instance) {
            is MainChild.Home ->
                HomeScreen(instance.component)
            
            is MainChild.Profile ->
                PlaceholderComposable()

            is MainChild.Speak ->
                SpeakScreen(instance.component)

            is MainChild.Voices ->
                VoicesScreen(instance.component)

            is MainChild.Settings ->
                SettingsScreen(instance.component)

            is MainChild.GuidedPractice ->
                GuidedPracticeScreen(instance.component)

            is MainChild.SpeakingModeSelection ->
                SpeakingModeSelectionScreen(instance.component)

            is MainChild.ConversationDetail ->
                ConversationDetailScreen(instance.component)

            is MainChild.ConversationList ->
                ConversationListScreen(instance.component)
        }
    }
}

@Composable
fun PlaceholderComposable() {

}