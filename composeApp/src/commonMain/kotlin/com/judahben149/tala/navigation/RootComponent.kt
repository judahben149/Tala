package com.judahben149.tala.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceAll
import com.arkivanov.decompose.value.Value
import com.judahben149.tala.domain.managers.SessionManager
import kotlinx.serialization.Serializable

class RootComponent(
    componentContext: ComponentContext,
    private val sessionManager: SessionManager
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<RootConfiguration>()

    val childStack: Value<ChildStack<*, RootChild>> = childStack(
        source = navigation,
        serializer = RootConfiguration.serializer(),
        initialConfiguration = RootConfiguration.Loading,
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun createChild(
        configuration: RootConfiguration,
        componentContext: ComponentContext
    ): RootChild = when (configuration) {
        is RootConfiguration.Loading -> RootChild.Loading

        is RootConfiguration.Onboarding -> RootChild.Onboarding(
            OnboardingFlowComponent(
                componentContext = componentContext,
                onOnboardingCompleted = ::navigateToMain
            )
        )

        is RootConfiguration.Main -> RootChild.Main(
            MainFlowComponent(
                componentContext = componentContext,
                onSignOut = ::navigateToOnboarding
            )
        )
    }

    fun checkAuthenticationState() {
        val appState = sessionManager.appState.value
        when (appState) {
            SessionManager.AppState.LoggedOut -> navigateToOnboarding()
            SessionManager.AppState.NeedsOnboarding -> navigateToOnboarding()
            SessionManager.AppState.LoggedIn -> navigateToMain()
            SessionManager.AppState.Unknown -> {
                // Stay in loading state
            }
        }
    }

    private fun navigateToOnboarding() {
        navigation.replaceAll(RootConfiguration.Onboarding)
    }

    private fun navigateToMain() {
        navigation.replaceAll(RootConfiguration.Main)
    }

    @Serializable
    sealed class RootConfiguration {
        @Serializable
        data object Loading : RootConfiguration()

        @Serializable
        data object Onboarding : RootConfiguration()

        @Serializable
        data object Main : RootConfiguration()
    }

    sealed class RootChild {
        data object Loading : RootChild()
        data class Onboarding(val component: OnboardingFlowComponent) : RootChild()
        data class Main(val component: MainFlowComponent) : RootChild()
    }
}