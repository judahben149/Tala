package com.judahben149.tala.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.judahben149.tala.domain.usecase.storage.StorageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PrefsTestComponent(
    componentContext: ComponentContext,
    private val navigation: StackNavigation<RootComponent.Config>
) : ComponentContext by componentContext, KoinComponent {
    
    private val storageUseCase: StorageUseCase by inject()
    private val _state = MutableStateFlow(PrefsTestState())
    val state: StateFlow<PrefsTestState> = _state.asStateFlow()
    
    fun onSaveString(key: String, value: String) {
        // storageUseCase.saveString(key, value)
        // For now, just update local state
        val currentStrings = _state.value.savedStrings.toMutableMap()
        currentStrings[key] = value
        _state.value = _state.value.copy(savedStrings = currentStrings)
    }
    
    fun onSaveInt(key: String, value: Int) {
        // storageUseCase.saveInt(key, value)
        // For now, just update local state
        val currentInts = _state.value.savedInts.toMutableMap()
        currentInts[key] = value
        _state.value = _state.value.copy(savedInts = currentInts)
    }
    
    fun onBackPressed() {
        navigation.pop()
    }
    
    data class PrefsTestState(
        val savedStrings: Map<String, String> = emptyMap(),
        val savedInts: Map<String, Int> = emptyMap(),
        val isLoading: Boolean = false,
        val error: String? = null
    )
}
