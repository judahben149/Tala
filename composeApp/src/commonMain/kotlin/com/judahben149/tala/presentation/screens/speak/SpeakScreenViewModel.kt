package com.judahben149.tala.presentation.screens.speak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.judahben149.tala.data.service.audio.SpeechPlayer
import com.judahben149.tala.domain.models.authentication.errors.NetworkException
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.speech.CharacterTimestamp
import com.judahben149.tala.domain.usecases.gemini.GenerateContentUseCase
import com.judahben149.tala.domain.usecases.speech.DownloadTextToSpeechUseCase
import com.judahben149.tala.domain.usecases.speech.GetAllVoicesUseCase
import com.judahben149.tala.domain.usecases.speech.GetFeaturedVoicesUseCase
import com.judahben149.tala.domain.usecases.speech.GetVoicesByGenderUseCase
import com.judahben149.tala.domain.usecases.speech.StreamTextToSpeechUseCase
import com.judahben149.tala.util.decodeBase64Audio
import com.judahben149.tala.util.mimeTypeForOutputFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SpeakScreenViewModel(
    private val generateContentUseCase: GenerateContentUseCase,
    private val streamTtsUseCase: StreamTextToSpeechUseCase,
    private val getAllVoicesUseCase: GetAllVoicesUseCase,
    private val getFeaturedVoicesUseCase: GetFeaturedVoicesUseCase,
    private val getVoicesByGenderUseCase: GetVoicesByGenderUseCase,
    private val streamTextToSpeechUseCase: StreamTextToSpeechUseCase,
    private val downloadTextToSpeechUseCase: DownloadTextToSpeechUseCase,
    private val logger: Logger,
    private val player: SpeechPlayer
) : ViewModel() {

    private val _userInput = MutableStateFlow("")
    val userInput: StateFlow<String> = _userInput

    private val _aiResponse = MutableStateFlow("")
    val aiResponse: StateFlow<String> = _aiResponse

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {

        viewModelScope.launch {
            val result = downloadTextToSpeechUseCase(
                text = "Heyy there, what are you doing? My young good son, in whom I am well pleased. You have done me well bro",
                voiceId = "21m00Tcm4TlvDq8ikWAM",
            )

            when(result) {
                is Result.Success -> {
//                    val bytes = decodeBase64Audio(result.data.audioBase64)
                    val bytes = withContext(Dispatchers.IO) { decodeBase64Audio(result.data.audioBase64) }
                    val mimeType = mimeTypeForOutputFormat("mp3_44100_128")

                    withContext(Dispatchers.Main) {
                        player.load(bytes, mimeType)
                        player.play()
                    }
                }

                is Result.Failure -> {
                    logger.d { "Error: ${result.error}" }
                }
            }
        }
    }

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
