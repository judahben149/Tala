package com.judahben149.tala.presentation.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.domain.models.conversation.Conversation
import com.judahben149.tala.domain.usecases.conversations.GetConversationHistoryUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConversationHistoryViewModel(
    private val getConversationsByUserUseCase: GetConversationHistoryUseCase,
    private val sessionManager: SessionManager,
    private val logger: Logger
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversationHistoryState())
    val uiState: StateFlow<ConversationHistoryState> = _uiState.asStateFlow()

    init {
        loadConversations()
    }

    private fun loadConversations() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                val userId = sessionManager.getUserId()
                getConversationsByUserUseCase(userId)
                    .catch { exception ->
                        logger.e(exception) { "Error loading conversations" }
                        _uiState.update { 
                            it.copy(
                                isLoading = false,
                                error = "Failed to load conversations: ${exception.message}"
                            )
                        }
                    }
                    .collect { conversations ->
                        logger.d { "Loaded ${conversations.size} conversations" }
                        val sortedConversations = conversations.sortedByDescending { it.updatedAt }
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                conversations = sortedConversations,
                                error = null
                            )
                        }
                    }
            } catch (e: Exception) {
                logger.e(e) { "Failed to get user ID or load conversations" }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load conversations: ${e.message}"
                    )
                }
            }
        }
    }

    fun refreshConversations() {
        logger.d { "Refreshing conversations" }
        loadConversations()
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun selectConversation(conversation: Conversation) {
        logger.d { "Selected conversation: ${conversation.id}" }
        // Navigation will be handled by the component
    }
}

data class ConversationHistoryState(
    val isLoading: Boolean = false,
    val conversations: List<Conversation> = emptyList(),
    val error: String? = null
)