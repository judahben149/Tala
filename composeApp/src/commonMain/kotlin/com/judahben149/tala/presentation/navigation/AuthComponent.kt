package com.judahben149.tala.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.push
import com.judahben149.tala.domain.usecase.auth.SignInUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class AuthComponent(
    componentContext: ComponentContext,
    private val navigation: StackNavigation<RootComponent.Config>
) : ComponentContext by componentContext, KoinComponent {
    
    private val signInUseCase: SignInUseCase by inject()
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state
    
    fun onSignInWithGoogle() {
        // TODO: Implement Google Sign In
        navigation.push(RootComponent.Config.Chat)
    }
    
    fun onSignInWithApple() {
        // TODO: Implement Apple Sign In
        navigation.push(RootComponent.Config.Chat)
    }
    
    fun onNavigateToRoomTest() {
        navigation.push(RootComponent.Config.RoomTest)
    }
    
    fun onNavigateToPrefsTest() {
        navigation.push(RootComponent.Config.PrefsTest)
    }
    
    data class AuthState(
        val isLoading: Boolean = false,
        val error: String? = null
    )
}
