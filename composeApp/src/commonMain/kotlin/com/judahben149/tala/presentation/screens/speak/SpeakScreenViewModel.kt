package com.judahben149.tala.presentation.screens.speak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.judahben149.tala.data.service.audio.SpeechPlayer
import com.judahben149.tala.data.service.permission.AudioPermissionManager
import com.judahben149.tala.domain.managers.MessageManager
import com.judahben149.tala.domain.managers.RemoteConfigManager
import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.domain.models.authentication.errors.NetworkException
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.conversation.GuidedPracticeScenario
import com.judahben149.tala.domain.models.conversation.SpeakingMode
import com.judahben149.tala.domain.models.speech.RecorderConfig
import com.judahben149.tala.domain.models.speech.RecorderStatus
import com.judahben149.tala.domain.models.user.AppUser
import com.judahben149.tala.domain.usecases.conversations.IncrementConversationCountUseCase
import com.judahben149.tala.domain.usecases.conversations.StartConversationUseCase
import com.judahben149.tala.domain.usecases.messages.AddAiMessageUseCase
import com.judahben149.tala.domain.usecases.messages.AddUserMessageUseCase
import com.judahben149.tala.domain.usecases.messages.GetConversationMessagesUseCase
import com.judahben149.tala.domain.usecases.speech.ConvertSpeechToTextUseCase
import com.judahben149.tala.domain.usecases.speech.DownloadTextToSpeechUseCase
import com.judahben149.tala.domain.usecases.speech.GetSelectedVoiceIdUseCase
import com.judahben149.tala.domain.usecases.speech.recording.*
import com.judahben149.tala.domain.usecases.user.ObservePersistedUserDataUseCase
import com.judahben149.tala.util.decodeBase64Audio
import com.judahben149.tala.util.getCurrentDateString
import com.judahben149.tala.util.mimeTypeForOutputFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SpeakScreenViewModel(
    private val startRecordingUseCase: StartRecordingUseCase,
    private val stopRecordingUseCase: StopRecordingUseCase,
    private val cancelRecordingUseCase: CancelRecordingUseCase,
    private val observeRecordingStatusUseCase: ObserveRecordingStatusUseCase,
    private val convertSpeechToTextUseCase: ConvertSpeechToTextUseCase,
    private val downloadTextToSpeechUseCase: DownloadTextToSpeechUseCase,
    private val addAiMessageUseCase: AddAiMessageUseCase,
    private val addUserMessageUseCase: AddUserMessageUseCase,
    private val startConversationUseCase: StartConversationUseCase,
    private val getSelectedVoiceIdUseCase: GetSelectedVoiceIdUseCase,
    private val observeAudioLevelsUseCase: ObserveAudioLevelsUseCase,
    private val incrementConversationCount: IncrementConversationCountUseCase,
    private val observePersistedUserDataUseCase: ObservePersistedUserDataUseCase,
    private val getConversationMessagesUseCase: GetConversationMessagesUseCase,
    private val messageManager: MessageManager,
    private val sessionManager: SessionManager,
    private val player: SpeechPlayer,
    private val audioPermissionManager: AudioPermissionManager,
    private val remoteConfigManager: RemoteConfigManager,
    private val logger: Logger
) : ViewModel() {

    private val _uiState = MutableStateFlow(SpeakScreenUiState())
    val uiState: StateFlow<SpeakScreenUiState> = _uiState.asStateFlow()

    private var conversationId: String? = null
    private var recordingStatus: RecorderStatus = RecorderStatus.Idle
    private var hasPermission = false
    private var currentUser: AppUser? = null

    init {
        observeRecordingStatus()
        observeAudioLevels()
        observeUserData()

        // Check if we already have a conversation ID from a previous session
        logger.d { "SpeakScreenViewModel initialized, conversationId: $conversationId" }

        // Debug: Log initial UI state
        logger.d { "[DEBUG_LOG] Initial UI state - messages: ${_uiState.value.messages.size}, conversationId: ${_uiState.value.conversationId}" }

        // If we have permission and no conversation, initialize one
        if (hasPermission && conversationId == null) {
            logger.d { "Auto-initializing conversation on startup" }
            initializeConversation()
        }
    }

    private fun observeConversationMessages() {
        conversationId?.let { id ->
            logger.d { "[DEBUG_LOG] Starting to observe conversation messages for conversation: $id" }

            viewModelScope.launch {
                try {
                    logger.d { "[DEBUG_LOG] Before collecting messages flow for conversation: $id" }

                    // Use the same approach as ConversationDetailViewModel
                    getConversationMessagesUseCase(id)
                        .catch { exception ->
                            logger.e(exception) { "[DEBUG_LOG] Error observing conversation messages in SpeakScreen" }
                            _uiState.update {
                                it.copy(
                                    error = "Failed to load messages: ${exception.message}"
                                )
                            }
                        }
                        .collect { messages ->
                            logger.d { "[DEBUG_LOG] Received ${messages.size} messages for conversation: $id" }
                            if (messages.isNotEmpty()) {
                                logger.d { "[DEBUG_LOG] First message: ${messages.first().content}, isUser: ${messages.first().isUser}" }
                                logger.d { "[DEBUG_LOG] All message IDs: ${messages.map { msg -> msg.id }}" }
                            } else {
                                logger.d { "[DEBUG_LOG] No messages received for conversation: $id" }
                            }

                            logger.d { "[DEBUG_LOG] Before updating UI state, current messages size: ${_uiState.value.messages.size}" }

                            _uiState.update { currentState ->
                                currentState.copy(
                                    messages = messages,
                                    conversationId = id
                                )
                            }

                            logger.d { "[DEBUG_LOG] After updating UI state, new messages size: ${_uiState.value.messages.size}" }
                        }

                    logger.d { "[DEBUG_LOG] Flow collection completed for conversation: $id" }
                } catch (e: Exception) {
                    logger.e(e) { "[DEBUG_LOG] Failed to observe messages for conversation: $id" }
                    _uiState.update {
                        it.copy(
                            error = "Failed to load messages: ${e.message}"
                        )
                    }
                }
            }
        } ?: logger.w { "[DEBUG_LOG] Cannot observe conversation messages: conversationId is null" }
    }

    fun updateSpeakingModes(
        speakingMode: SpeakingMode,
        scenario: GuidedPracticeScenario? = null
    ) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    speakingMode = speakingMode,
                    scenario = scenario
                )
            }
        }
    }

    private fun observeRecordingStatus() {
        observeRecordingStatusUseCase()
            .onEach { status ->
                recordingStatus = status
                logger.d { "Recording status changed: $status" }
            }
            .launchIn(viewModelScope)
    }

    private fun observeAudioLevels() {
        combine(
            observeAudioLevelsUseCase.observeAudioLevel(),
            observeAudioLevelsUseCase.observeAudioLevel().map { level ->
                observeAudioLevelsUseCase.isSpeaking(level, threshold = 0.15f)
            }
        ) { audioLevel, isSpeaking ->
            audioLevel to isSpeaking
        }.onEach { (audioLevel, isSpeaking) ->
//            logger.d { "Audio level updated: $audioLevel" }
            _uiState.update { currentState ->
                currentState.copy(
                    audioLevel = audioLevel,
                    isSpeaking = isSpeaking
                )
            }
        }.launchIn(viewModelScope)
    }

    private fun observeUserData() {
        viewModelScope.launch {
            observePersistedUserDataUseCase()
                .filterNotNull()
                .catch { exception ->
                    logger.e(exception) { "Error observing user data in SpeakScreen" }
                }
                .collect { appUser ->
                    currentUser = appUser
                    logger.d { "User data updated in SpeakScreen: ${appUser.displayName}" }

                    // Check quota status and update conversation state if needed
                    checkQuotaStatus(appUser)
                }
        }
    }

    private fun checkQuotaStatus(user: AppUser) {
        if (user.isPremiumUser) {
            return
        }

        val today = getCurrentDateString()
        val shouldReset = user.messageDailyQuotaCountLastResetDate != today
        val currentQuota = if (shouldReset) 0 else user.messageQuotaCount

        if (currentQuota >= 10) {
            // User has exceeded quota
            _uiState.update { currentState ->
                if (currentState.conversationState != ConversationState.Disallowed) {
                    currentState.copy(conversationState = ConversationState.Disallowed)
                } else {
                    currentState
                }
            }
            logger.w { "User quota exceeded: $currentQuota/10" }
        }
    }

    fun onPermissionGranted() {
        hasPermission = true
        logger.d { "[DEBUG_LOG] Microphone permission granted" }
        clearError()

        if (conversationId == null) {
            logger.d { "[DEBUG_LOG] No existing conversation, initializing new conversation after permission granted" }
            initializeConversation()
        } else {
            logger.d { "[DEBUG_LOG] Using existing conversation: $conversationId" }
            // Make sure we're observing messages for the existing conversation
            logger.d { "[DEBUG_LOG] Current UI state before observing existing conversation - messages: ${_uiState.value.messages.size}, conversationId: ${_uiState.value.conversationId}" }
            observeConversationMessages()
            logger.d { "[DEBUG_LOG] Called observeConversationMessages for existing conversation: $conversationId" }
        }
    }

    fun onPermissionDenied() {
        hasPermission = false
        logger.w { "Microphone permission denied" }
        updateError("Microphone permission is required to record audio")
        updateState(conversationState = ConversationState.Stopped, isLoading = false)
    }

    fun initializeConversation() {
        viewModelScope.launch {
            updateState(isLoading = true)
            logger.d { "Initializing conversation..." }

            try {
                val userId = sessionManager.getUserId()
                val language = sessionManager.getUserLanguagePreference().name
                logger.d { "Starting conversation for user: $userId, language: $language" }

                when (val result = startConversationUseCase(userId, language, "General")) {
                    is Result.Success -> {
                        conversationId = result.data
                        logger.d { "Conversation initialized successfully: ${result.data}" }
                        updateState(isLoading = false)

                        // Call observeConversationMessages() before startRecording()
                        logger.d { "[DEBUG_LOG] Starting to observe messages for new conversation: ${result.data}" }
                        logger.d { "[DEBUG_LOG] Current conversationId before observeConversationMessages: $conversationId" }
                        observeConversationMessages()

                        // Check if messages are being observed
                        logger.d { "[DEBUG_LOG] Current UI state messages size after observeConversationMessages: ${_uiState.value.messages.size}" }

//                        startRecording()
                    }
                    is Result.Failure -> {
                        logger.e { "Failed to initialize conversation: ${result.error}" }
                        updateError("Failed to start conversation: ${result.error.message}")
                        updateState(conversationState = ConversationState.Stopped, isLoading = false)
                    }
                }
            } catch (e: Exception) {
                logger.e(e) { "Exception during conversation initialization" }
                updateError("Failed to initialize: ${e.message}")
                updateState(conversationState = ConversationState.Stopped, isLoading = false)
            }
        }
    }

    fun onButtonClicked() {
        when (_uiState.value.conversationState) {
            ConversationState.Idle -> startRecording()
            ConversationState.Recording -> stopRecording()
            ConversationState.Speaking -> interruptSpeaking()
            ConversationState.Stopped -> restartConversation()
            else -> {
                logger.d { "Button click ignored for state: ${_uiState.value.conversationState}" }
            }
        }
    }

    fun onButtonPressed() {
        when (_uiState.value.conversationState) {
            ConversationState.Idle, ConversationState.Stopped -> startRecording()
            else -> {
                logger.d { "Button press ignored for state: ${_uiState.value.conversationState}" }
            }
        }
    }

    fun onButtonReleased() {
        when (_uiState.value.conversationState) {
            ConversationState.Recording -> stopRecording()
            else -> {
                logger.d { "Button release ignored for state: ${_uiState.value.conversationState}" }
            }
        }
    }

    private fun startRecording() {
        if (!hasPermission) {
            logger.w { "Cannot start recording without permission" }
            updateError("Microphone permission required")
            return
        }

        val currentState = _uiState.value.conversationState
        if (currentState != ConversationState.Idle && currentState != ConversationState.Stopped) {
            logger.w { "Cannot start recording from state: $currentState" }
            return
        }

        viewModelScope.launch {
            updateState(conversationState = ConversationState.Recording)
            clearError()

            val config = RecorderConfig(
                sampleRate = 16_000,
                channelCount = 1,
                bitsPerSample = 16,
                wrapAsWav = true
            )

            when (val result = startRecordingUseCase(config)) {
                is Result.Success -> {
                    logger.d { "Recording started successfully" }
                }
                is Result.Failure -> {
                    logger.e { "Failed to start recording: ${result.error}" }
                    updateError("Failed to start recording: ${result.error.message}")
                    updateState(conversationState = ConversationState.Stopped)
                }
            }
        }
    }

    private fun stopRecording() {
        if (recordingStatus != RecorderStatus.Recording) {
            logger.w { "Cannot stop recording, current status: $recordingStatus" }
            return
        }

        viewModelScope.launch {
            updateState(conversationState = ConversationState.Converting)

            when (val result = stopRecordingUseCase()) {
                is Result.Success -> {
                    val (audioBytes, base64Audio) = result.data
                    _uiState.update { it.copy(recordedAudio = audioBytes) }
                    logger.d { "Recording stopped successfully, audio size: ${audioBytes.size} bytes" }
                    processAudioToText(audioBytes, base64Audio)
                }
                is Result.Failure -> {
                    logger.e { "Failed to stop recording: ${result.error}" }
                    updateError("Failed to stop recording: ${result.error.message}")
                    updateState(conversationState = ConversationState.Stopped)
                }
            }
        }
    }

    private suspend fun processAudioToText(audioBytes: ByteArray, base64Audio: String) {
        try {
            val language = sessionManager.getUserLanguagePreference()
            logger.d { "Starting speech-to-text conversion for ${language.name}" }

            when (val result = convertSpeechToTextUseCase(audioBytes = audioBytes, language = language)) {
                is Result.Success -> {
                    val transcribedText = result.data.text
                    logger.d { "Speech successfully converted to text: $transcribedText" }
                    generateAIResponse(transcribedText, base64Audio)
                }
                is Result.Failure -> {
                    logger.e { "Speech-to-text conversion failed: ${result.error}" }
                    handleNetworkError(result.error)
                    updateState(conversationState = ConversationState.Stopped)
                }
            }
        } catch (e: Exception) {
            logger.e(e) { "Exception during speech-to-text processing" }
            updateError("Speech processing failed: ${e.message}")
            updateState(conversationState = ConversationState.Stopped)
        }
    }

    private suspend fun generateAIResponse(userText: String, userAudioBase64: String) {
        val convId = conversationId
        if (convId == null) {
            logger.e { "No active conversation for AI response generation" }
            updateError("No active conversation")
            updateState(conversationState = ConversationState.Stopped)
            return
        }

        // Check quota before processing
        val user = currentUser
        if (user != null && !user.isPremiumUser) {
            val today = getCurrentDateString()
            val shouldReset = user.messageDailyQuotaCountLastResetDate != today
            val currentQuota = if (shouldReset) 0 else user.messageQuotaCount

            if (currentQuota >= remoteConfigManager.getLong("daily_message_limit", 10L)) {
                logger.w { "Quota exceeded, blocking message" }
//                updateError("Daily message limit reached. Upgrade to Premium for unlimited messages.")
                updateError("Daily message limit reached. Please check back tomorrow for renewed access.")
                updateState(conversationState = ConversationState.Disallowed)
                return
            }
        }

        try {
            updateState(conversationState = ConversationState.Thinking)
            logger.d { "Generating AI response for: $userText" }

            when (
                val result = messageManager.generateResponse(
                    conversationId = convId,
                    userInput = userText,
                    guidedPracticeScenario = uiState.value.scenario
                )
            ) {
                is Result.Success -> {
                    val aiResponse = result.data
                    logger.d { "AI response generated successfully: $aiResponse" }

                    logger.d { "[DEBUG_LOG] Adding user message to conversation: $convId, text: $userText" }
                    addUserMessageUseCase(convId, userText, userAudioBase64)
                    incrementConversationCount()  // This will now be checked server-side
                    logger.d { "[DEBUG_LOG] After adding user message, current UI state messages size: ${_uiState.value.messages.size}" }
                    convertTextToSpeech(aiResponse, convId)
                }
                is Result.Failure -> {
                    logger.e { "AI response generation failed: ${result.error}" }

                    // Check if it's a quota error
                    if (result.error.message?.contains("quota") == true ||
                        result.error.message?.contains("limit") == true) {
                        updateState(conversationState = ConversationState.Disallowed)
                    } else {
                        updateState(conversationState = ConversationState.Stopped)
                    }
                    updateError("Failed to generate response: ${result.error.message}")
                }
            }
        } catch (e: Exception) {
            logger.e(e) { "Exception during AI response generation" }
            updateError("AI processing failed: ${e.message}")
            updateState(conversationState = ConversationState.Stopped)
        }
    }

    private suspend fun convertTextToSpeech(text: String, convId: String) {
        try {
            updateState(conversationState = ConversationState.Speaking)
            logger.d { "Converting text to speech: $text" }

            when (val result = downloadTextToSpeechUseCase(text, getSelectedVoiceIdUseCase())) {
                is Result.Success -> {
                    logger.d { "Text-to-speech conversion successful" }
                    val audioBase64 = result.data.audioBase64
                    if (audioBase64 != null) {
                        logger.d { "[DEBUG_LOG] Adding AI message to conversation: $convId, text: $text" }
                        addAiMessageUseCase(convId, text, audioBase64)
                        logger.d { "[DEBUG_LOG] After adding AI message, current UI state messages size: ${_uiState.value.messages.size}" }
                        playAISpeech(audioBase64)
                    } else {
                        logger.e { "Text-to-speech conversion returned null audio data" }
                        logger.d { "[DEBUG_LOG] Adding AI message (without audio) to conversation: $convId, text: $text" }
                        addAiMessageUseCase(convId, text, null)
                        logger.d { "[DEBUG_LOG] After adding AI message (without audio), current UI state messages size: ${_uiState.value.messages.size}" }
                        updateError("Failed to generate speech: Audio data is missing")
                        updateState(conversationState = ConversationState.Stopped)
                    }
                }
                is Result.Failure -> {
                    logger.e { "Text-to-speech conversion failed: ${result.error}" }
                    logger.d { "[DEBUG_LOG] Adding AI message (after TTS failure) to conversation: $convId, text: $text" }
                    addAiMessageUseCase(convId, text, null)
                    logger.d { "[DEBUG_LOG] After adding AI message (after TTS failure), current UI state messages size: ${_uiState.value.messages.size}" }
                    updateError("Failed to generate speech: ${result.error.message}")
                    updateState(conversationState = ConversationState.Stopped)
                }
            }
        } catch (e: Exception) {
            logger.e(e) { "Exception during text-to-speech conversion" }
            logger.d { "[DEBUG_LOG] Adding AI message (after TTS exception) to conversation: $convId, text: $text" }
            addAiMessageUseCase(convId, text, null)
            logger.d { "[DEBUG_LOG] After adding AI message (after TTS exception), current UI state messages size: ${_uiState.value.messages.size}" }
            updateError("Speech generation failed: ${e.message}")
            updateState(conversationState = ConversationState.Stopped)
        }
    }

    private suspend fun playAISpeech(audioBase64: String) {
        try {
            logger.d { "Starting AI speech playback" }

            val audioBytes = withContext(Dispatchers.IO) {
                decodeBase64Audio(audioBase64)
            }
            val mimeType = mimeTypeForOutputFormat("mp3_44100_128")

            withContext(Dispatchers.Main) {
                player.load(audioBytes, mimeType)
                player.play()
            }

            logger.d { "AI speech playback completed" }
            updateState(conversationState = ConversationState.Idle)
        } catch (e: Exception) {
            logger.e(e) { "Failed to play AI speech" }
            updateError("Failed to play response: ${e.message}")
            updateState(conversationState = ConversationState.Stopped)
        }
    }

    private fun interruptSpeaking() {
        viewModelScope.launch {
            try {
                logger.d { "Interrupting AI speech" }

                withContext(Dispatchers.Main) {
                    player.stop()
                }

                logger.d { "AI speech interrupted successfully" }
                updateState(conversationState = ConversationState.Idle)
                startRecording()
            } catch (e: Exception) {
                logger.e(e) { "Failed to interrupt speech" }
                updateError("Failed to interrupt: ${e.message}")
            }
        }
    }

    private fun restartConversation() {
        logger.d { "Restarting conversation" }
        clearError()
        updateState(conversationState = ConversationState.Idle)
        startRecording()
    }

    fun cancelRecording() {
        viewModelScope.launch {
            logger.d { "Cancelling recording" }

            when (val result = cancelRecordingUseCase()) {
                is Result.Success -> {
                    updateState(conversationState = ConversationState.Idle)
                    logger.d { "Recording cancelled successfully" }
                }
                is Result.Failure -> {
                    logger.e { "Failed to cancel recording: ${result.error}" }
                    updateError("Failed to cancel recording: ${result.error.message}")
                }
            }
        }
    }

    private fun updateState(
        conversationState: ConversationState? = null,
        isLoading: Boolean? = null
    ) {
        _uiState.update { currentState ->
            currentState.copy(
                conversationState = conversationState ?: currentState.conversationState,
                isLoading = isLoading ?: currentState.isLoading
            )
        }
    }

    private fun updateError(message: String) {
        _uiState.update { it.copy(error = message) }
        logger.e { "Error updated: $message" }
    }

    private fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun handleNetworkError(error: NetworkException) {
        val errorMessage = when (error) {
            is NetworkException.Unauthorized -> "Authentication failed. Please check your API key."
            is NetworkException.BadRequest -> "Invalid request: ${error.message}"
            is NetworkException.Timeout -> "Request timed out. Please try again."
            is NetworkException.IO -> "Network connection error. Check your internet connection."
            is NetworkException.ServerError -> "Server error occurred. Please try again later."
            is NetworkException.NotFound -> "Service not found. Please try again."
            is NetworkException.Forbidden -> "Access denied. Please check your permissions."
            is NetworkException.Serialization -> "Data processing error occurred."
            is NetworkException.InvalidResponse -> "Invalid response received."
            else -> "Network error: ${error.message ?: "Unknown error"}"
        }
        updateError(errorMessage)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch {
            try {
                withContext(Dispatchers.Main) {
                    player.stop()
                }
            } catch (e: Exception) {
                logger.e(e) { "Error stopping player in onCleared" }
            }
        }
        logger.d { "SpeakScreenViewModel cleared" }
    }
}
