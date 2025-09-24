package com.judahben149.tala.presentation.screens.history.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.judahben149.tala.data.service.audio.SpeechPlayer
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.conversation.Conversation
import com.judahben149.tala.domain.models.conversation.ConversationMessage
import com.judahben149.tala.domain.usecases.conversations.GetAudioFileUseCase
import com.judahben149.tala.domain.usecases.conversations.GetActiveConversationUseCase
import com.judahben149.tala.domain.usecases.messages.GetConversationMessagesUseCase
import com.judahben149.tala.util.mimeTypeForOutputFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ConversationDetailViewModel(
    private val getConversationUseCase: GetActiveConversationUseCase,
    private val getMessagesUseCase: GetConversationMessagesUseCase,
    private val getAudioFileUseCase: GetAudioFileUseCase,
    private val speechPlayer: SpeechPlayer,
    private val logger: Logger
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConversationDetailState())
    val uiState: StateFlow<ConversationDetailState> = _uiState.asStateFlow()

    private var currentConversationId: String? = null

    fun loadConversation(conversationId: String) {
        if (currentConversationId == conversationId && !_uiState.value.messages.isEmpty()) {
            logger.d { "Conversation already loaded: $conversationId" }
            return
        }

        currentConversationId = conversationId

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            try {
                combine(
                    getConversationUseCase(""), // We'll need to modify this to get by ID
                    getMessagesUseCase(conversationId)
                ) { conversation, messages ->
                    conversation to messages
                }.catch { exception ->
                    logger.e(exception) { "Error loading conversation data" }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = "Failed to load conversation: ${exception.message}"
                        )
                    }
                }.collect { (conversation, messages) ->
                    logger.d { "Loaded conversation with ${messages.size} messages" }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            conversation = conversation,
                            messages = messages,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                logger.e(e) { "Failed to load conversation: $conversationId" }
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = "Failed to load conversation: ${e.message}"
                    )
                }
            }
        }
    }

    fun playUserAudio(message: ConversationMessage) {
        val audioPath = message.userAudioPath
        if (audioPath == null) {
            logger.w { "No user audio path for message: ${message.id}" }
            return
        }

        playAudio(message.id, audioPath, isUserAudio = true)
    }

    fun playAiAudio(message: ConversationMessage) {
        val audioPath = message.aiAudioPath
        if (audioPath == null) {
            logger.w { "No AI audio path for message: ${message.id}" }
            return
        }

        playAudio(message.id, audioPath, isUserAudio = false)
    }

    private fun playAudio(messageId: String, audioPath: String, isUserAudio: Boolean) {
        val currentlyPlaying = _uiState.value.currentlyPlayingMessageId

        // Stop current audio if playing
        if (currentlyPlaying != null) {
            stopAudio()
        }

        // If clicking the same audio that was playing, just stop
        if (currentlyPlaying == messageId) {
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    currentlyPlayingMessageId = messageId,
                    isLoadingAudio = true,
                    audioError = null,
                    audioProgress = 0f,
                    audioDuration = 0f,
                    isAudioPaused = false
                )
            }

            when (val result = getAudioFileUseCase(audioPath)) {
                is Result.Success -> {
                    try {
                        val mimeType = if (isUserAudio) {
                            "audio/wav" // User recordings are typically WAV
                        } else {
                            mimeTypeForOutputFormat("mp3_44100_128") // AI audio is MP3
                        }

                        withContext(Dispatchers.Main) {
                            speechPlayer.load(result.data, mimeType)
                            speechPlayer.play()
                        }

                        // Add a small delay to ensure the player has initialized
                        kotlinx.coroutines.delay(300)

                        // Get the duration after loading
                        var duration = speechPlayer.getDuration()
                        logger.d { "Initial audio duration for message $messageId: $duration seconds" }

                        // If duration is 0, try a few more times with increasing delays
                        if (duration <= 0f) {
                            for (i in 1..5) {
                                kotlinx.coroutines.delay(200 * i.toLong())
                                duration = speechPlayer.getDuration()
                                logger.d { "Retry $i: Audio duration for message $messageId: $duration seconds" }
                                if (duration > 0f) break
                            }
                        }

                        // Only update with valid duration
                        if (duration > 0f) {
                            _uiState.update {
                                it.copy(
                                    isLoadingAudio = false,
                                    audioDuration = duration
                                )
                            }
                            logger.d { "Updated UI state with duration: $duration seconds" }
                        } else {
                            // If we still couldn't get a valid duration, update UI state but log a warning
                            _uiState.update {
                                it.copy(
                                    isLoadingAudio = false,
                                    audioDuration = 30f  // Use a default duration as fallback
                                )
                            }
                            logger.w { "Could not get valid duration for message $messageId, using default" }
                        }

                        // Start progress tracking
                        val progressJob = launch {
                            while (speechPlayer.isPlaying() && _uiState.value.currentlyPlayingMessageId != null) {
                                val progress = speechPlayer.getCurrentPosition()

                                // Log progress periodically (every second)
                                if ((progress * 10).toInt() % 10 == 0) {
                                    logger.d { "Audio progress for message $messageId: $progress / $duration seconds" }
                                }

                                _uiState.update {
                                    it.copy(audioProgress = progress)
                                }

                                // Check if playback has completed
                                if (progress >= duration - 0.1f) {
                                    _uiState.update {
                                        it.copy(
                                            currentlyPlayingMessageId = null,
                                            audioProgress = 0f
                                        )
                                    }
                                    break
                                }

                                kotlinx.coroutines.delay(100) // Update every 100ms
                            }
                        }

                        // Monitor playback completion
                        launch {
                            while (speechPlayer.isPlaying() && _uiState.value.currentlyPlayingMessageId != null) {
                                kotlinx.coroutines.delay(500) // Check every 500ms
                            }

                            // If we get here and the player is not playing, but we still have a currentlyPlayingMessageId,
                            // it means playback completed naturally
                            if (!speechPlayer.isPlaying() && _uiState.value.currentlyPlayingMessageId != null) {
                                _uiState.update {
                                    it.copy(
                                        currentlyPlayingMessageId = null,
                                        audioProgress = 0f
                                    )
                                }
                                progressJob.cancel()
                            }
                        }

                        logger.d { "Started playing audio for message: $messageId" }

                    } catch (e: Exception) {
                        logger.e(e) { "Failed to play audio for message: $messageId" }
                        _uiState.update {
                            it.copy(
                                currentlyPlayingMessageId = null,
                                isLoadingAudio = false,
                                audioError = "Failed to play audio: ${e.message}",
                                audioProgress = 0f,
                                audioDuration = 0f
                            )
                        }
                    }
                }
                is Result.Failure -> {
                    logger.e { "Failed to get audio file: ${result.error}" }
                    _uiState.update {
                        it.copy(
                            currentlyPlayingMessageId = null,
                            isLoadingAudio = false,
                            audioError = "Failed to load audio: ${result.error.message}",
                            audioProgress = 0f,
                            audioDuration = 0f
                        )
                    }
                }
            }
        }
    }

    fun pauseAudio() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.Main) {
                    speechPlayer.pause()
                }
                _uiState.update {
                    it.copy(
                        isAudioPaused = true
                    )
                }
                logger.d { "Paused audio playback" }
            } catch (e: Exception) {
                logger.e(e) { "Failed to pause audio playback" }
            }
        }
    }

    fun resumeAudio() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.Main) {
                    speechPlayer.play()
                }
                _uiState.update {
                    it.copy(
                        isAudioPaused = false
                    )
                }
                logger.d { "Resumed audio playback" }
            } catch (e: Exception) {
                logger.e(e) { "Failed to resume audio playback" }
            }
        }
    }

    fun stopAudio() {
        viewModelScope.launch {
            try {
                withContext(Dispatchers.Main) {
                    speechPlayer.stop()
                }
                _uiState.update {
                    it.copy(
                        currentlyPlayingMessageId = null,
                        isLoadingAudio = false,
                        audioProgress = 0f,
                        audioDuration = 0f,
                        isAudioPaused = false
                    )
                }
                logger.d { "Stopped audio playback" }
            } catch (e: Exception) {
                logger.e(e) { "Failed to stop audio playback" }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun clearAudioError() {
        _uiState.update { it.copy(audioError = null) }
    }

    fun retryLoad() {
        currentConversationId?.let { id ->
            loadConversation(id)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            try {
                withContext(Dispatchers.Main) {
                    speechPlayer.stop()
                }
            } catch (e: Exception) {
                logger.e(e) { "Error stopping player in onCleared" }
            }
        }
        logger.d { "ConversationDetailViewModel cleared" }
    }
}

data class ConversationDetailState(
    val isLoading: Boolean = false,
    val conversation: Conversation? = null,
    val messages: List<ConversationMessage> = emptyList(),
    val currentlyPlayingMessageId: String? = null,
    val isLoadingAudio: Boolean = false,
    val error: String? = null,
    val audioError: String? = null,
    val audioProgress: Float = 0f, // Current position in seconds
    val audioDuration: Float = 0f,  // Total duration in seconds
    val isAudioPaused: Boolean = false // Track if audio is paused
)
