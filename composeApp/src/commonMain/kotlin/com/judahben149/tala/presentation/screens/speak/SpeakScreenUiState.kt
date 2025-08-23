package com.judahben149.tala.presentation.screens.speak

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector

data class SpeakScreenUiState(
    val conversationState: ConversationState = ConversationState.Idle,
    val error: String? = null,
    val isLoading: Boolean = false,
    val recordedAudio: ByteArray? = null
) {
    val buttonLabel: String
        get() = when (conversationState) {
            ConversationState.Idle -> "Tap to talk"
            ConversationState.Recording -> "Listening..."
            ConversationState.Converting -> "Processing speech..."
            ConversationState.Thinking -> "Thinking..."
            ConversationState.Speaking -> "Replying..."
            ConversationState.Stopped -> "Stopped"
        }

    val buttonAction: String
        get() = when (conversationState) {
            ConversationState.Idle -> "Speak"
            ConversationState.Recording -> "Stop"
            ConversationState.Converting -> "Converting"
            ConversationState.Thinking -> "Please wait"
            ConversationState.Speaking -> "⏹Stop reply"
            ConversationState.Stopped -> "Start over"
        }

    val buttonIcon: ImageVector
        get() = when (conversationState) {
            ConversationState.Idle -> Icons.Outlined.Mic
            ConversationState.Recording -> Icons.Outlined.Stop
            ConversationState.Converting -> Icons.Outlined.Sync
            ConversationState.Thinking -> Icons.Outlined.Psychology
            ConversationState.Speaking -> Icons.Outlined.Stop
            ConversationState.Stopped -> Icons.Outlined.Refresh
        }

    val isButtonEnabled: Boolean
        get() = when (conversationState) {
            ConversationState.Converting, ConversationState.Thinking -> false
            else -> !isLoading
        }

    val canInterrupt: Boolean
        get() = conversationState == ConversationState.Speaking
}