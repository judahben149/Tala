package com.judahben149.tala.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
import com.judahben149.tala.navigation.components.HomeScreenComponent
import com.judahben149.tala.navigation.components.LoginScreenComponent
import com.judahben149.tala.navigation.components.SignUpScreenComponent

class RootComponent(
    componentContext: ComponentContext
): ComponentContext by componentContext {

    private val navigation = StackNavigation<Configuration>()

    val childStack = childStack(
        source = navigation,
        serializer = Configuration.serializer(),
        initialConfiguration = Configuration.SignUpScreen,
        handleBackButton = true,
        childFactory = ::createChild
    )

    private fun createChild(configuration: Configuration, componentContext: ComponentContext): Child =
        when (configuration) {

            is Configuration.SignUpScreen -> Child.SignUpScreen(
                SignUpScreenComponent(
                    componentContext = componentContext,
                    onButtonClick = { textFromFirstScreen ->
                        navigation.pushNew(Configuration.LoginScreen)
                    },
                    onNavigateToLogin = { navigation.bringToFront(Configuration.LoginScreen) },
                    onNavigateToHome = { navigation navigateTo Configuration.HomeScreen }
                )
            )

            is Configuration.LoginScreen -> Child.LoginScreen(
                LoginScreenComponent(
                    componentContext = componentContext,
                    onBackButtonClick = { navigation.pop() },
                    onNavigateToHome = { navigation navigateTo Configuration.HomeScreen },
                    onNavigateToSignUp = { navigation.bringToFront(Configuration.SignUpScreen) }
                )
            )

            is Configuration.HomeScreen -> Child.HomeScreen(
                HomeScreenComponent(
                    componentContext = componentContext,
                    onBackButtonClick = { navigation.pop() }
                )
            )
        }

    fun navigateToHome() {
        navigation navigateTo Configuration.HomeScreen
    }

    fun navigateToLogin() {
        navigation.bringToFront(Configuration.LoginScreen)
    }

    fun navigateToSignUp() {
        navigation.bringToFront(Configuration.SignUpScreen)
    }


    private infix fun StackNavigation<Configuration>.navigateTo(configuration: Configuration) {
        this.navigate { stack ->
            if (stack.lastOrNull()?.let { it::class } != configuration::class) {
                stack + configuration
            } else {
                stack
            }
        }
    }

    sealed class Child {
        data class SignUpScreen(val component: SignUpScreenComponent) : Child()
        data class LoginScreen(val component: LoginScreenComponent) : Child()

        data class HomeScreen(val component: HomeScreenComponent) : Child()
    }
}