package com.judahben149.tala.presentation.screens.speak

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.judahben149.tala.domain.models.conversation.GuidedPracticeScenario
import com.judahben149.tala.domain.models.conversation.SpeakingMode

data class SpeakScreenUiState(
    val conversationState: ConversationState = ConversationState.Idle,
    val error: String? = null,
    val isLoading: Boolean = false,
    val recordedAudio: ByteArray? = null,
    val permissionRequired: Boolean = false,
    val audioLevel: Float = 0f,
    val isSpeaking: Boolean = false,
    val speakingMode: SpeakingMode = SpeakingMode.FREE_SPEAK,
    val scenario: GuidedPracticeScenario? = null
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

    val voiceLevelForAnimation: Float
        get() = if (conversationState == ConversationState.Recording) {
            0.1f + (audioLevel * 0.65f)
        } else {
            0.1f
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as SpeakScreenUiState

        if (isLoading != other.isLoading) return false
        if (conversationState != other.conversationState) return false
        if (error != other.error) return false
        if (!recordedAudio.contentEquals(other.recordedAudio)) return false
        if (isButtonEnabled != other.isButtonEnabled) return false
        if (canInterrupt != other.canInterrupt) return false
        if (buttonLabel != other.buttonLabel) return false
        if (buttonAction != other.buttonAction) return false
        if (buttonIcon != other.buttonIcon) return false
        if (audioLevel != other.audioLevel) return false
        if (isSpeaking != other.isSpeaking) return false
        if (speakingMode != other.speakingMode) return false
        if (scenario != other.scenario) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isLoading.hashCode()
        result = 31 * result + conversationState.hashCode()
        result = 31 * result + (error?.hashCode() ?: 0)
        result = 31 * result + (recordedAudio?.contentHashCode() ?: 0)
        result = 31 * result + isButtonEnabled.hashCode()
        result = 31 * result + canInterrupt.hashCode()
        result = 31 * result + buttonLabel.hashCode()
        result = 31 * result + buttonAction.hashCode()
        result = 31 * result + buttonIcon.hashCode()
        result = 31 * result + audioLevel.hashCode()
        result = 31 * result + isSpeaking.hashCode()
        result = 31 * result + speakingMode.hashCode()
        result = 31 * result + scenario.hashCode()
        return result
    }
}