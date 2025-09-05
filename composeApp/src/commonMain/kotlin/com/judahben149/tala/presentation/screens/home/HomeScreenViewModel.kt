package com.judahben149.tala.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.domain.models.user.AppUser
import com.judahben149.tala.domain.usecases.user.ObservePersistedUserDataUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeScreenViewModel(
    private val sessionManager: SessionManager,
    private val observePersistedUser: ObservePersistedUserDataUseCase,
    private val logger: Logger
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeScreenState())
    val uiState: StateFlow<HomeScreenState> = _uiState.asStateFlow()

    fun isVoicesSelectionComplete(): Boolean = sessionManager.isVoiceSelectionCompleted()

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            try {
                // Load user data
                if (observePersistedUser.hasPersistedUser()) {
                    val user = observePersistedUser.getCurrentUser()

                    // Load learning language
                    val learningLanguage = user.learningLanguage

                    // Load recent topics from preferences/cache
                    val recentTopics = getRecentTopics()

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            user = user,
                            userName = user.displayName.split(" ").first(),
                            streakDays = user?.streakDays ?: 0,
                            totalConversations = user.totalConversations,
                            learningLanguage = learningLanguage,
                            weeklyGoalProgress = calculateWeeklyProgress(user.totalConversations),
                            recentTopics = recentTopics
                        )
                    }
                } else {
                    return@launch
                }
            } catch (e: Exception) {
                logger.e(e) { "Failed to load home data" }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load data: ${e.message}",
                        userName = "Friend"
                    )
                }
            }
        }
    }

    fun isVoiceSelectionComplete(): Boolean {
        return sessionManager.isVoiceSelectionCompleted()
    }

    private fun getRecentTopics(): List<String> {
        // TODO: Implement actual recent topics loading from database/preferences
        // For now, return dummy data based on user interests
        val userInterests = sessionManager.getUserInterests()
        return if (userInterests.isNotEmpty()) {
            userInterests.take(4).toList()
        } else {
            listOf("Travel", "Food", "Technology", "Culture")
        }
    }

    private fun calculateWeeklyProgress(totalConversations: Int): Float {
        // TODO: Implement actual weekly progress calculation
        // For now, return a dummy progress based on total conversations
        val weeklyGoal = 7 // conversations per week
        val thisWeekConversations = minOf(totalConversations % 10, weeklyGoal)
        return thisWeekConversations.toFloat() / weeklyGoal.toFloat()
    }

    fun refreshData() {
        loadHomeData()
    }
}

data class HomeScreenState(
    val isLoading: Boolean = false,
    val user: AppUser? = null,
    val userName: String = "",
    val streakDays: Int = 0,
    val totalConversations: Int = 0,
    val learningLanguage: String = "Spanish",
    val weeklyGoalProgress: Float = 0f,
    val recentTopics: List<String> = emptyList(),
    val error: String? = null
)
