package com.judahben149.tala.presentation.screens.speak.guidedPractice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.judahben149.tala.domain.models.conversation.GuidedPracticeScenario
import com.judahben149.tala.domain.models.conversation.MasteryLevel
import com.judahben149.tala.domain.repository.ConversationRepository
import com.judahben149.tala.domain.usecases.conversations.GetMasteryLevelUseCase
import com.judahben149.tala.util.GuidedPracticeCard
import com.judahben149.tala.util.GuidedPracticeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GuidedPracticeViewModel(
    private val getMasteryLevelUseCase: GetMasteryLevelUseCase,
    private val repository: ConversationRepository
) : ViewModel() {
    
    private val _scenarios = MutableStateFlow<List<GuidedPracticeCard>>(emptyList())
    val scenarios: StateFlow<List<GuidedPracticeCard>> = _scenarios.asStateFlow()
    
    private val _userMasteryLevel = MutableStateFlow(MasteryLevel.BEGINNER)
    val userMasteryLevel: StateFlow<MasteryLevel> = _userMasteryLevel.asStateFlow()
    
    init {
        loadScenarios()
        loadUserMasteryLevel()
    }
    
    private fun loadScenarios() {
        viewModelScope.launch {
            val userLevel = getMasteryLevelUseCase()
            val availableScenarios = GuidedPracticeUtils.getScenariosForLevel(userLevel)

            val scenarioCards = availableScenarios.map { scenario ->
                GuidedPracticeCard(
                    scenario = scenario,
                    isUnlocked = scenario.difficulty <= userLevel,
                    isCompleted = false, // TODO: Get from database
                    completionCount = 0  // TODO: Get from database
                )
            }

            _scenarios.value = scenarioCards
        }
    }
    
    private fun loadUserMasteryLevel() {
        viewModelScope.launch {
            _userMasteryLevel.value = getMasteryLevelUseCase()
        }
    }
    
    fun startGuidedPractice(scenario: GuidedPracticeScenario) {
        // Navigate to conversation screen with guided practice mode
        // Pass the scenario to the conversation
    }
    
    fun getRecommendedScenario(): GuidedPracticeScenario? {
        val completedIds = emptyList<String>() // TODO: Get from database
        return GuidedPracticeUtils.recommendNextScenario(
            completedIds,
            _userMasteryLevel.value
        )
    }
}