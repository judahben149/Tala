package com.judahben149.tala.presentation.screens.voices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.judahben149.tala.data.service.audio.SpeechPlayer
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.usecases.speech.*
import com.judahben149.tala.util.decodeBase64Audio
import com.judahben149.tala.util.mimeTypeForOutputFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VoicesScreenViewModel(
    private val getAllVoicesUseCase: GetAllVoicesUseCase,
    private val getFeaturedVoicesUseCase: GetFeaturedVoicesUseCase,
    private val downloadTextToSpeechUseCase: DownloadTextToSpeechUseCase,
    private val saveSelectedVoiceUseCase: SaveSelectedVoiceUseCase,
    private val setVoiceSelectionCompleteUseCase: SetVoiceSelectionCompleteUseCase,
    private val player: SpeechPlayer,
    private val logger: Logger
) : ViewModel() {

    private val _uiState = MutableStateFlow(VoicesUiState())
    val uiState: StateFlow<VoicesUiState> = _uiState.asStateFlow()

    private val samplePhrases = listOf(
        "Hello! I'm Tala, your AI language tutor.",
        "Let's practice speaking together and improve your skills.",
        "I'm here to help you become fluent in your target language.",
        "Ready to start your language learning journey?",
        "Great choice! Let's begin practicing conversations.",
        "I'll help you speak with confidence and clarity.",
        "Your pronunciation practice starts here with me.",
        "Welcome to Tala! I'm excited to be your speaking partner."
    )

    init {
        loadVoices()
    }

    private fun loadVoices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            when (val result = getFeaturedVoicesUseCase()) {
                is Result.Success -> {
                    _uiState.update { 
                        it.copy(
                            voices = result.data,
                            isLoading = false,
                            selectedIndex = 0
                        )
                    }
                    if (result.data.isNotEmpty()) {
                        playSamplePhrase(0)
                    }
                }
                is Result.Failure -> {
                    _uiState.update { 
                        it.copy(
                            isLoading = false,
                            error = "Failed to load voices: ${result.error.message}"
                        )
                    }
                    logger.e { "Failed to load voices: ${result.error}" }
                }
            }
        }
    }

    fun onVoiceSelected(index: Int) {
        val voices = _uiState.value.voices
        if (index in voices.indices) {
            _uiState.update { it.copy(selectedIndex = index) }
            playSamplePhrase(index)
        }
    }

    private fun playSamplePhrase(voiceIndex: Int) {
        val voices = _uiState.value.voices
        if (voiceIndex !in voices.indices) return

        val voice = voices[voiceIndex]
        val phraseIndex = voiceIndex % samplePhrases.size
        val phrase = samplePhrases[phraseIndex]

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isPlayingSample = true) }
                
                when (val result = downloadTextToSpeechUseCase(phrase, voice.voiceId)) {
                    is Result.Success -> {
                        val audioBytes = withContext(Dispatchers.IO) { 
                            decodeBase64Audio(result.data.audioBase64 ?: "")
                        }
                        val mimeType = mimeTypeForOutputFormat("mp3_44100_128")

                        withContext(Dispatchers.Main) {
                            player.load(audioBytes, mimeType)
                            player.play()
                        }
                        
                        logger.d { "Playing sample for voice: ${voice.name}" }
                    }
                    is Result.Failure -> {
                        logger.e { "Failed to generate sample audio: ${result.error}" }
                    }
                }
            } catch (e: Exception) {
                logger.e(e) { "Error playing sample phrase" }
            } finally {
                _uiState.update { it.copy(isPlayingSample = false) }
            }
        }
    }

    fun saveSelectedVoice(onComplete: () -> Unit) {
        val voices = _uiState.value.voices
        val selectedIndex = _uiState.value.selectedIndex
        
        if (selectedIndex in voices.indices) {
            val selectedVoice = voices[selectedIndex]
            
            viewModelScope.launch {
                try {
                    saveSelectedVoiceUseCase(selectedVoice.voiceId)
                    logger.d { "Saved selected voice: ${selectedVoice.name}" }
                    setVoiceSelectionCompleteUseCase()
                    onComplete()
                } catch (e: Exception) {
                    logger.e(e) { "Failed to save selected voice" }
                    _uiState.update { 
                        it.copy(error = "Failed to save voice selection: ${e.message}") 
                    }
                }
            }
        }
    }

    fun retryLoading() {
        loadVoices()
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
    }
}