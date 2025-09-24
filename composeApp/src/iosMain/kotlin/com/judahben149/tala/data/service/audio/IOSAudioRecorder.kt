package com.judahben149.tala.data.service.audio

import com.judahben149.tala.domain.models.speech.RecorderConfig
import com.judahben149.tala.domain.models.speech.RecorderStatus
import com.judahben149.tala.util.AudioLevelCalculator
import com.judahben149.tala.util.WavEncoder
import kotlinx.cinterop.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import platform.AVFAudio.*
import platform.Foundation.*

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
class IOSAudioRecorder : SpeechRecorder {

    companion object {
        private const val TAG = "IOSAudioRecorder"
    }

    private val _status = MutableStateFlow(RecorderStatus.Idle)
    override val status: StateFlow<RecorderStatus> = _status.asStateFlow()

    // Audio level monitoring
    private val _audioLevel = MutableStateFlow(0f)
    override val audioLevel: StateFlow<Float> = _audioLevel.asStateFlow()

    private val _peakLevel = MutableStateFlow(0f)
    override val peakLevel: StateFlow<Float> = _peakLevel.asStateFlow()

    private var audioEngine: AVAudioEngine? = null
    private var inputNode: AVAudioInputNode? = null
    private var recordingJob: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var recordedData = mutableListOf<ByteArray>()
    private val recordingMutex = Mutex()
    private var currentConfig: RecorderConfig = RecorderConfig()

    private var smoothedLevel = 0f
    private var currentPeak = 0f

    override suspend fun start(config: RecorderConfig) {
        if (_status.value == RecorderStatus.Recording) {
            println("$TAG: Recording already in progress")
            return
        }

        try {
            currentConfig = config

            // Reset level monitoring state
            smoothedLevel = 0f
            currentPeak = 0f
            _audioLevel.value = 0f
            _peakLevel.value = 0f
            
            setupAudioSession()
            setupAudioEngine()
            _status.value = RecorderStatus.Recording

        } catch (e: Exception) {
            println("$TAG: Failed to start recording: ${e.message}")
            _status.value = RecorderStatus.Error
            cleanup()
        }
    }

    override suspend fun stop(): ByteArray = withContext(Dispatchers.Default) {
        if (_status.value != RecorderStatus.Recording) {
            println("$TAG: Not currently recording")
            return@withContext ByteArray(0)
        }

        _status.value = RecorderStatus.Stopped

        try {
            audioEngine?.stop()
            recordingJob?.join()

            // Combine all recorded chunks using mutex
            val combinedData = recordingMutex.withLock {
                val totalSize = recordedData.sumOf { it.size }
                val combined = ByteArray(totalSize)
                var offset = 0

                for (chunk in recordedData) {
                    chunk.copyInto(combined, offset)
                    offset += chunk.size
                }
                combined
            }

            println("$TAG: Recording stopped. Total data size: ${combinedData.size} bytes")

            return@withContext if (currentConfig.wrapAsWav && combinedData.isNotEmpty()) {
                WavEncoder.pcm16ToWavMono(combinedData, currentConfig.sampleRate)
            } else {
                combinedData
            }

        } catch (e: Exception) {
            println("$TAG: Error stopping recording: ${e.message}")
            _status.value = RecorderStatus.Error
            return@withContext ByteArray(0)
        } finally {
            cleanup()
        }
    }

    override suspend fun cancel() {
        println("$TAG: Cancelling recording")
        _status.value = RecorderStatus.Stopped
        recordingJob?.cancel()
        cleanup()
    }

    private fun setupAudioSession() {
        val audioSession = AVAudioSession.sharedInstance()

        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()

            // Set category to record
            val categorySet = audioSession.setCategory(AVAudioSessionCategoryRecord, error.ptr)
            if (!categorySet) {
                error.value?.let {
                    throw Exception("Failed to set audio session category: ${it.localizedDescription}")
                }
            }

            // Set mode
            val modeSet = audioSession.setMode(AVAudioSessionModeDefault, error.ptr)
            if (!modeSet) {
                error.value?.let {
                    throw Exception("Failed to set audio session mode: ${it.localizedDescription}")
                }
            }

            // Activate session
            val activated = audioSession.setActive(true, error.ptr)
            if (!activated) {
                error.value?.let {
                    throw Exception("Failed to activate audio session: ${it.localizedDescription}")
                }
            }
        }
    }

    private fun setupAudioEngine() {
        recordedData.clear()
        audioEngine = AVAudioEngine()
        inputNode = audioEngine?.inputNode

        val audioEngine = this.audioEngine ?: throw Exception("Failed to create audio engine")
        val inputNode = this.inputNode ?: throw Exception("Failed to get input node")

        // Get the hardware format (must be used for tap)
        val inputFormat = inputNode.outputFormatForBus(0u)

// Install tap using *hardware* format
        inputNode.installTapOnBus(
            bus = 0u,
            bufferSize = 1024u,
            format = inputFormat
        ) { buffer, _ ->
            buffer?.let { audioBuffer ->
                convertAndStoreAudioBuffer(audioBuffer)
            }
        }

        // Start audio engine
        memScoped {
            val error = alloc<ObjCObjectVar<NSError?>>()
            val started = audioEngine.startAndReturnError(error.ptr)
            if (!started) {
                error.value?.let {
                    throw Exception("Failed to start audio engine: ${it.localizedDescription}")
                }
            }
        }
    }

    private fun convertAndStoreAudioBuffer(buffer: AVAudioPCMBuffer) {
        val frameLength = buffer.frameLength.toInt()
        val channelData = buffer.floatChannelData ?: return
        val channels = buffer.format.channelCount.toInt()

        val floatArray = FloatArray(frameLength * channels)
        for (frame in 0 until frameLength) {
            for (ch in 0 until channels) {
                floatArray[frame * channels + ch] = channelData[ch]!![frame]
            }
        }

        // Calculate audio level from float samples before downsampling
        val currentLevel = AudioLevelCalculator.calculateRMS(floatArray)

        // Apply smoothing and update peak
        smoothedLevel = AudioLevelCalculator.smoothLevel(currentLevel, smoothedLevel)
        currentPeak = AudioLevelCalculator.updatePeak(smoothedLevel, currentPeak)

        // Update StateFlows on main thread
        scope.launch {
            _audioLevel.value = smoothedLevel
            _peakLevel.value = currentPeak
        }

        // Downsample: 48000 → 16000 (simple decimation: take every 3rd sample)
        val downsampled = floatArray.filterIndexed { i, _ -> i % 3 == 0 }

        // Convert Float32 → Int16
        val byteArray = ByteArray(downsampled.size * 2)
        for ((i, sample) in downsampled.withIndex()) {
            val intSample = (sample * Short.MAX_VALUE).toInt()
                .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                .toShort()
            byteArray[i * 2] = (intSample.toInt() and 0xFF).toByte()
            byteArray[i * 2 + 1] = ((intSample.toInt() shr 8) and 0xFF).toByte()
        }

        scope.launch {
            recordingMutex.withLock {
                recordedData.add(byteArray)
            }
        }
    }


    private fun cleanup() {
        try {
            audioEngine?.stop()
            inputNode?.removeTapOnBus(0u)
            audioEngine = null
            inputNode = null
            recordedData.clear()

            // Reset levels
            _audioLevel.value = 0f
            _peakLevel.value = 0f
            smoothedLevel = 0f
            currentPeak = 0f
            _status.value = RecorderStatus.Idle

            // Deactivate audio session
            memScoped {
                val error = alloc<ObjCObjectVar<NSError?>>()
                AVAudioSession.sharedInstance().setActive(false, error.ptr)
            }
        } catch (e: Exception) {
            println("$TAG: Error during cleanup: ${e.message}")
        }
    }
}