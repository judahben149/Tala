package com.judahben149.tala.navigation.flow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.judahben149.tala.navigation.MainFlowComponent
import com.judahben149.tala.presentation.screens.home.HomeScreen
import com.judahben149.tala.presentation.screens.speak.SpeakScreen
import com.judahben149.tala.presentation.screens.voices.VoicesScreen

@Composable
fun MainFlow(component: MainFlowComponent) {
    val childStack by component.childStack.subscribeAsState()
    
    Children(
        stack = childStack,
        animation = stackAnimation(slide())
    ) { child ->
        when (val instance = child.instance) {
            is MainFlowComponent.MainChild.Home -> 
                HomeScreen(instance.component)
            
            is MainFlowComponent.MainChild.Profile ->
                PlaceholderComposable()
//                ProfileScreen(instance.component)
            is MainFlowComponent.MainChild.Speak ->
                SpeakScreen(instance.component)

            is MainFlowComponent.MainChild.Voices ->
                VoicesScreen(instance.component)
        }
    }
}

@Composable
fun PlaceholderComposable() {

}