package com.judahben149.tala.navigation.flow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.arkivanov.decompose.extensions.compose.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.subscribeAsState
import com.judahben149.tala.navigation.OnboardingFlowComponent
import com.judahben149.tala.presentation.screens.login.LoginScreen
import com.judahben149.tala.presentation.screens.signUp.SignUpScreen

@Composable
fun OnboardingFlow(component: OnboardingFlowComponent) {
    val childStack by component.childStack.subscribeAsState()
    
    Children(
        stack = childStack,
        animation = stackAnimation(slide())
    ) { child ->
        when (val instance = child.instance) {
            is OnboardingFlowComponent.OnboardingChild.SignUp -> 
                SignUpScreen(instance.component)
            
            is OnboardingFlowComponent.OnboardingChild.Login -> 
                LoginScreen(instance.component)
            
            is OnboardingFlowComponent.OnboardingChild.Welcome ->
                PlaceholderComposable()
//                WelcomeScreen(instance.component)
            
            is OnboardingFlowComponent.OnboardingChild.LanguageSelection ->
                PlaceholderComposable()
//                LanguageSelectionScreen(instance.component)
            
            is OnboardingFlowComponent.OnboardingChild.InterestsSelection ->
                PlaceholderComposable()
//                InterestsSelectionScreen(instance.component)
        }
    }
}