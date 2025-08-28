package com.judahben149.tala.presentation.screens.signUp.interests

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.user.Interest
import com.judahben149.tala.domain.usecases.preferences.SaveUserInterestsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class InterestsSelectionViewModel(
    private val saveUserInterestsUseCase: SaveUserInterestsUseCase,
    private val sessionManager: SessionManager,
    private val logger: Logger
) : ViewModel() {

    private val _uiState = MutableStateFlow(InterestsSelectionState())
    val uiState: StateFlow<InterestsSelectionState> = _uiState.asStateFlow()

    fun loadInterests() {
        val interests = listOf(
            Interest("Travel", Icons.Default.Flight),
            Interest("Food & Cooking", Icons.Default.Restaurant),
            Interest("Business", Icons.Default.Business),
            Interest("Technology", Icons.Default.Computer),
            Interest("Sports", Icons.Default.SportsFootball),
            Interest("Music", Icons.Default.MusicNote),
            Interest("Movies", Icons.Default.Movie),
            Interest("Art", Icons.Default.Palette),
            Interest("Health & Fitness", Icons.Default.FitnessCenter),
            Interest("Fashion", Icons.Default.BabyChangingStation),
            Interest("Education", Icons.Default.School),
            Interest("Science", Icons.Default.Science),
            Interest("History", Icons.Default.HistoryEdu),
            Interest("Culture", Icons.Default.Museum),
            Interest("Environment", Icons.Default.Nature),
            Interest("Gaming", Icons.Default.SportsEsports)
        )
        
        _uiState.update { it.copy(availableInterests = interests) }
    }

    fun toggleInterest(interest: Interest) {
        _uiState.update { currentState ->
            val updatedInterests = if (currentState.selectedInterests.contains(interest)) {
                currentState.selectedInterests - interest
            } else {
                currentState.selectedInterests + interest
            }
            currentState.copy(selectedInterests = updatedInterests)
        }
    }

    fun saveSelectedInterests() {
        val selectedInterests = _uiState.value.selectedInterests
        if (selectedInterests.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val interestNames = selectedInterests.map { it.name }

            // Save both locally and remotely
            try {
                // Save locally first (always succeeds)
                sessionManager.saveUserInterests(interestNames)

                // Then save remotely
                when (val result = saveUserInterestsUseCase(interestNames)) {
                    is Result.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        logger.d { "Interests saved locally and remotely: $interestNames" }
                    }
                    is Result.Failure -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = "Saved locally, but failed to sync: ${result.error.message}"
                            )
                        }
                        logger.w { "Interests saved locally but remote save failed: ${result.error}" }
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to save interests: ${e.message}"
                    )
                }
                logger.e(e) { "Failed to save interests" }
            }
        }
    }

    fun loadSavedInterests() {
        viewModelScope.launch {
            // Load from local preferences for immediate display
            val savedInterests = sessionManager.getUserInterests()

            if (savedInterests.isNotEmpty()) {
                val availableInterests = _uiState.value.availableInterests
                val selectedInterestObjects = availableInterests.filter {
                    savedInterests.contains(it.name)
                }.toSet()

                _uiState.update {
                    it.copy(selectedInterests = selectedInterestObjects)
                }
            }
        }
    }
}

data class InterestsSelectionState(
    val availableInterests: List<Interest> = emptyList(),
    val selectedInterests: Set<Interest> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)
