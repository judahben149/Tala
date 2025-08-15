package com.judahben149.tala.presentation.screens.speak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.judahben149.tala.domain.models.authentication.errors.NetworkException
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.speech.CharacterTimestamp
import com.judahben149.tala.domain.usecases.gemini.GenerateContentUseCase
import com.judahben149.tala.domain.usecases.speech.StreamTextToSpeechUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SpeakScreenViewModel(
    private val generateContentUseCase: GenerateContentUseCase,
    private val streamTtsUseCase: StreamTextToSpeechUseCase,
    private val logger: Logger
) : ViewModel() {

    private val _userInput = MutableStateFlow("")
    val userInput: StateFlow<String> = _userInput

    private val _aiResponse = MutableStateFlow("")
    val aiResponse: StateFlow<String> = _aiResponse

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun onUserInputChange(text: String) {
        _userInput.value = text
    }

    fun sendMessage() {
        val prompt = _userInput.value.trim()
        logger.d { "Prompt: $prompt" }
        if (prompt.isEmpty()) return

        viewModelScope.launch {
            _isLoading.value = true
            _aiResponse.value = ""
            try {
                when (val result = generateContentUseCase(prompt, emptyList())) {
                    is Result.Success -> {
                        _aiResponse.value = result.data.candidates[0].content.parts[0].text
                    }
                    is Result.Failure -> {
                        _aiResponse.value = when (result.error) {
                            is NetworkException -> "Network error: ${result.error.message}"
                            else -> "Unknown error occurred"
                        }
                    }
                }
            } catch (e: Exception) {
                _aiResponse.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun startTextToSpeech(
        text: String,
        voiceId: String,
        apiKey: String
    ) {
        val result = streamTtsUseCase(
            text = text,
            voiceId = voiceId,
            apiKey = apiKey,
            stability = 0.75f,
            similarityBoost = 0.75f,
            useSpeakerBoost = true
        )

        when (result) {
            is Result.Success -> {
                result.data.collect { audioChunk ->
                    // Play audio chunk
                    playAudioChunk(audioChunk.audioData)

                    // Update UI with character timing
                    updateTimestamps(audioChunk.timestamps)
                }
            }
            is Result.Failure -> {
                // Handle error
//                handleTtsError(result.error)
            }
        }
    }

    private fun playAudioChunk(audioData: ByteArray) {
        // Implement audio playback logic
    }

    private fun updateTimestamps(timestamps: List<CharacterTimestamp>) {
        // Update UI with character-level timing for text highlighting
    }
}
