package com.judahben149.tala.presentation.screens.speak

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.judahben149.tala.data.service.audio.SpeechPlayer
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.speech.RecorderConfig
import com.judahben149.tala.domain.models.speech.RecorderStatus
import com.judahben149.tala.domain.usecases.gemini.GenerateContentUseCase
import com.judahben149.tala.domain.usecases.speech.ConvertSpeechToTextUseCase
import com.judahben149.tala.domain.usecases.speech.DownloadTextToSpeechUseCase
import com.judahben149.tala.domain.usecases.speech.recording.CancelRecordingUseCase
import com.judahben149.tala.domain.usecases.speech.recording.ObserveRecordingStatusUseCase
import com.judahben149.tala.domain.usecases.speech.recording.StartRecordingUseCase
import com.judahben149.tala.domain.usecases.speech.recording.StopRecordingUseCase
import com.judahben149.tala.util.decodeBase64Audio
import com.judahben149.tala.util.mimeTypeForOutputFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * This ViewModel focuses on:
 * - Recording audio as WAV (wrapAsWav = true)
 * - Letting the user play back the recorded file locally to verify it’s playable
 *
 * It does not invoke STT or TTS. It’s purely for capture-and-play verification.
 */
class SpeakScreenViewModel(
    private val startRecordingUseCase: StartRecordingUseCase,
    private val stopRecordingUseCase: StopRecordingUseCase,
    private val cancelRecordingUseCase: CancelRecordingUseCase,
    private val observeRecordingStatusUseCase: ObserveRecordingStatusUseCase,
    private val convertSpeechToTextUseCase: ConvertSpeechToTextUseCase,
    private val downloadTextToSpeechUseCase: DownloadTextToSpeechUseCase,
    private val generateContentUseCase: GenerateContentUseCase,
    private val player: SpeechPlayer,
    private val logger: Logger
) : ViewModel() {

    private val _recordingStatus = MutableStateFlow(RecorderStatus.Idle)
    val recordingStatus: StateFlow<RecorderStatus> = _recordingStatus

    private val _isRecordingLoading = MutableStateFlow(false)
    val isRecordingLoading: StateFlow<Boolean> = _isRecordingLoading

    private val _recordingError = MutableStateFlow<String?>(null)
    val recordingError: StateFlow<String?> = _recordingError

    private val _recordedAudioBytes = MutableStateFlow<ByteArray?>(null)
    val recordedAudioBytes: StateFlow<ByteArray?> = _recordedAudioBytes

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _mimeType = MutableStateFlow("audio/wav")
    val mimeType: StateFlow<String> = _mimeType

    init {
        // Observe recording status and keep UI updated
        observeRecordingStatusUseCase()
            .onEach { status ->
                _recordingStatus.value = status
                logger.d { "Recording status: $status" }
            }
            .launchIn(viewModelScope)
    }

    fun startRecording() {
        if (_recordingStatus.value == RecorderStatus.Recording || _isRecordingLoading.value) return

        viewModelScope.launch {
            _isRecordingLoading.value = true
            _recordingError.value = null
            _recordedAudioBytes.value = null

            // Force WAV wrapping here so we can play back easily and upload as audio/wav
            val config = RecorderConfig(
                sampleRate = 16_000,
                channelCount = 1,
                bitsPerSample = 16,
                wrapAsWav = true
            )

            when (val res = startRecordingUseCase(config)) {
                is Result.Success -> {
                    logger.d { "Recording started (16kHz mono 16-bit, WAV)" }
                    _isRecordingLoading.value = false
                }
                is Result.Failure -> {
                    _isRecordingLoading.value = false
                    _recordingError.value = res.error.message ?: "Failed to start recording"
                    logger.e { "Failed to start recording: ${res.error}" }
                }
            }
        }
    }

    fun stopRecording() {
        if (_recordingStatus.value != RecorderStatus.Recording || _isRecordingLoading.value) return

        viewModelScope.launch {
            _isRecordingLoading.value = true
            when (val res = stopRecordingUseCase()) {

                is Result.Success -> {
                    val (wavBytes, base64Audio) = res.data

                    _isRecordingLoading.value = false
                    _recordedAudioBytes.value = wavBytes
                    _mimeType.value = "audio/wav"

                    // Optional quick local verification: attempt to load into player (no play yet)
                    try {
                        withContext(Dispatchers.Main) {
                            player.load(wavBytes, _mimeType.value)


                            when(val result = convertSpeechToTextUseCase(wavBytes)) {
                                is Result.Success -> {


                                    when(
                                        val result = generateContentUseCase(
                                            result.data.text,
                                            emptyList(),
                                        )
                                    ) {
                                        is Result.Success -> {
                                            generateAndPlaySpeech(result.data.candidates[0].content.parts[0].text)
                                        }

                                        is Result.Failure -> {

                                        }
                                    }
                                }

                                is Result.Failure -> {
                                    logger.d { "Failed to convert speech to text" }
                                }
                            }
                        }
                        logger.d { "WAV loaded into player successfully (size=${wavBytes.size} bytes)" }
                    } catch (t: Throwable) {
                        logger.e(t) { "Failed to load WAV into player" }
                        _recordingError.value = "Failed to load audio: ${t.message}"
                    }
                }
                is Result.Failure -> {
                    _isRecordingLoading.value = false
                    _recordingError.value = res.error.message ?: "Failed to stop recording"
                    logger.e { "Failed to stop recording: ${res.error}" }
                }
            }
        }
    }

    fun cancelRecording() {
        if (_recordingStatus.value != RecorderStatus.Recording) return

        viewModelScope.launch {
            when (val res = cancelRecordingUseCase()) {
                is Result.Success -> {
                    _isRecordingLoading.value = false
                    _recordingError.value = null
                    logger.d { "Recording cancelled" }
                }
                is Result.Failure -> {
                    _isRecordingLoading.value = false
                    _recordingError.value = res.error.message ?: "Failed to cancel recording"
                    logger.e { "Failed to cancel recording: ${res.error}" }
                }
            }
        }
    }

    fun playRecorded() {
        val bytes = _recordedAudioBytes.value ?: run {
            _recordingError.value = "No recorded audio available"
            return
        }

        viewModelScope.launch(Dispatchers.Main) {
            try {
                // If already loaded in stopRecording(), load is optional; call play directly.
                player.load(bytes, _mimeType.value) // safe to reload
                player.play()
                _isPlaying.value = true
                logger.d { "Playback started" }
            } catch (t: Throwable) {
                _isPlaying.value = false
                logger.e(t) { "Playback failed" }
                _recordingError.value = "Playback failed: ${t.message}"
            }
        }
    }

    fun stopPlayback() {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                player.stop()
                _isPlaying.value = false
                logger.d { "Playback stopped" }
            } catch (t: Throwable) {
                logger.e(t) { "Stopping playback failed" }
            }
        }
    }

    fun clearRecording() {
        _recordedAudioBytes.value = null
        _recordingError.value = null
        _isPlaying.value = false
        logger.d { "Cleared recorded audio buffer" }
    }

    private suspend fun generateAndPlaySpeech(text: String) {
        val result = downloadTextToSpeechUseCase(
            text = text,
            voiceId = "21m00Tcm4TlvDq8ikWAM",
        )

        when(result) {
            is Result.Success -> {
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
