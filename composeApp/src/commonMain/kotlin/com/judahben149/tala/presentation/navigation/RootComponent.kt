package com.judahben149.tala.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import kotlinx.serialization.Serializable

class RootComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    
    private val navigation = StackNavigation<Config>()
    
    val childStack: Value<ChildStack<Config, Child>> = childStack(
        source = navigation,
        serializer = Config.serializer(),
        initialConfiguration = Config.Auth,
        handleBackButton = true,
        childFactory = ::createChild
    )
    
    private fun createChild(
        config: Config,
        componentContext: ComponentContext
    ): Child = when (config) {
        is Config.Auth -> Child.Auth(AuthComponent(componentContext, navigation))
        is Config.Chat -> Child.Chat(ChatComponent(componentContext, navigation))
        is Config.RoomTest -> Child.RoomTest(RoomTestComponent(componentContext, navigation))
        is Config.PrefsTest -> Child.PrefsTest(PrefsTestComponent(componentContext, navigation))
    }
    
    sealed class Child {
        data class Auth(val component: AuthComponent) : Child()
        data class Chat(val component: ChatComponent) : Child()
        data class RoomTest(val component: RoomTestComponent) : Child()
        data class PrefsTest(val component: PrefsTestComponent) : Child()
    }
    
    @Serializable
    sealed class Config {
        @Serializable
        data object Auth : Config()
        
        @Serializable
        data object Chat : Config()
        
        @Serializable
        data object RoomTest : Config()
        
        @Serializable
        data object PrefsTest : Config()
    }
}
