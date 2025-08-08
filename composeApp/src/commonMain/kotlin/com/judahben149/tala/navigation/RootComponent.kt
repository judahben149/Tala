package com.judahben149.tala.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.pushNew
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
                        navigation.pushNew(Configuration.LoginScreen(text = textFromFirstScreen))
                    }
                )
            )

            is Configuration.LoginScreen -> Child.LoginScreen(
                LoginScreenComponent(
                    componentContext = componentContext,
                    text = configuration.text,
                    onBackButtonClick = { navigation.pop() }
                )
            )
        }


    sealed class Child {
        data class SignUpScreen(val component: SignUpScreenComponent) : Child()
        data class LoginScreen(val component: LoginScreenComponent) : Child()
    }
}