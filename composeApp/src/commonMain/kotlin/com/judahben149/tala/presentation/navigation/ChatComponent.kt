package com.judahben149.tala.presentation.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.pop
import com.judahben149.tala.domain.model.ChatMessage
import com.judahben149.tala.domain.usecase.chat.ChatUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ChatComponent(
    componentContext: ComponentContext,
    private val navigation: StackNavigation<RootComponent.Config>
) : ComponentContext by componentContext, KoinComponent {
    
    private val chatUseCase: ChatUseCase by inject()
    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()
    
    init {
        // Connect to chat when component is created
        // chatUseCase.connectToChat()
    }
    
    fun onSendMessage(text: String) {
        // chatUseCase.sendMessage(text)
        // For now, just add to local state
        val newMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            text = text,
            senderId = "current_user",
            senderName = "You",
            timestamp = kotlinx.datetime.Clock.System.now(),
            isFromCurrentUser = true
        )
        
        val currentMessages = _state.value.messages.toMutableList()
        currentMessages.add(newMessage)
        _state.value = _state.value.copy(messages = currentMessages)
    }
    
    fun onBackPressed() {
        navigation.pop()
    }
    
    data class ChatState(
        val messages: List<ChatMessage> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )
}
