package com.judahben149.tala.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import com.judahben149.tala.navigation.components.top.HomeScreenComponent
import com.judahben149.tala.navigation.components.others.ProfileScreenComponent
import com.judahben149.tala.navigation.components.others.SpeakScreenComponent
import com.judahben149.tala.navigation.components.others.VoicesScreenComponent
import kotlinx.serialization.Serializable

class MainFlowComponent(
    componentContext: ComponentContext,
    private val onSignOut: () -> Unit
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<MainConfiguration>()

    val childStack: Value<ChildStack<*, MainChild>> = childStack(
        source = navigation,
        serializer = MainConfiguration.serializer(),
        initialConfiguration = MainConfiguration.Home,
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun createChild(
        configuration: MainConfiguration,
        componentContext: ComponentContext
    ): MainChild = when (configuration) {
        
        is MainConfiguration.Home -> MainChild.Home(
            HomeScreenComponent(
                componentContext = componentContext,
                onNavigateToProfile = { 
                    navigation.pushNew(MainConfiguration.Profile) 
                },
                onNavigateToSpeak = {
                    navigation.pushNew(MainConfiguration.Speak)
                },
                onNavigateToVoices = {
                    navigation.pushNew(MainConfiguration.Voices)
                }
            )
        )

        is MainConfiguration.Profile -> MainChild.Profile(
            ProfileScreenComponent(
                componentContext = componentContext,
                onSignOut = onSignOut,
                onBackPressed = { navigation.pop() }
            )
        )

        is MainConfiguration.Voices -> MainChild.Voices(
            VoicesScreenComponent(
                componentContext = componentContext,
                onVoiceSelected = { navigation.pushNew(MainConfiguration.Speak) },
                onBackPressed = { navigation.pop() }
            )
        )

        is MainConfiguration.Speak -> MainChild.Speak(
            SpeakScreenComponent(
                componentContext = componentContext,
                onViewConversationList = {

                },
                onBackPressed = { navigation.pop() }
            )
        )
    }

    @Serializable
    sealed class MainConfiguration {
        @Serializable
        data object Home : MainConfiguration()
        
        @Serializable
        data object Profile : MainConfiguration()

        @Serializable
        data object Speak : MainConfiguration()

        @Serializable
        data object Voices : MainConfiguration()
    }

    sealed class MainChild {
        data class Home(val component: HomeScreenComponent) : MainChild()
        data class Profile(val component: ProfileScreenComponent) : MainChild()
        data class Speak(val component: SpeakScreenComponent) : MainChild()
        data class Voices(val component: VoicesScreenComponent) : MainChild()
    }
}