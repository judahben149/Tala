package com.judahben149.tala.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.judahben149.tala.domain.model.TestEntity
import com.judahben149.tala.domain.usecase.storage.StorageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class RoomTestComponent(
    componentContext: ComponentContext,
    private val navigation: StackNavigation<RootComponent.Config>
) : ComponentContext by componentContext, KoinComponent {
    
    private val storageUseCase: StorageUseCase by inject()
    private val _state = MutableStateFlow(RoomTestState())
    val state: StateFlow<RoomTestState> = _state.asStateFlow()
    
    fun onAddEntity(name: String, description: String) {
        val entity = TestEntity(
            name = name,
            description = description,
            createdAt = Clock.System.now()
        )
        // storageUseCase.insertTestEntity(entity)
        // For now, just add to local state
        val currentEntities = _state.value.entities.toMutableList()
        currentEntities.add(entity.copy(id = currentEntities.size + 1))
        _state.value = _state.value.copy(entities = currentEntities)
    }
    
    fun onDeleteEntity(entity: TestEntity) {
        // storageUseCase.deleteTestEntity(entity)
        // For now, just remove from local state
        val currentEntities = _state.value.entities.toMutableList()
        currentEntities.remove(entity)
        _state.value = _state.value.copy(entities = currentEntities)
    }
    
    fun onBackPressed() {
        navigation.pop()
    }
    
    data class RoomTestState(
        val entities: List<TestEntity> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )
}
