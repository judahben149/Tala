package com.judahben149.tala.data.service.audio

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.judahben149.tala.domain.models.speech.RecorderConfig
import com.judahben149.tala.domain.models.speech.RecorderStatus
import com.judahben149.tala.util.AudioLevelCalculator
import com.judahben149.tala.util.WavEncoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class AndroidAudioRecorder : SpeechRecorder {

    companion object {
        private const val TAG = "AndroidAudioRecorder"
        private const val RECORDING_TIMEOUT_MS = 60_000L // 1 minute max
        private const val LEVEL_UPDATE_INTERVAL_MS = 50L
    }

    private val _status = MutableStateFlow(RecorderStatus.Idle)
    override val status: StateFlow<RecorderStatus> = _status.asStateFlow()

    private val _audioLevel = MutableStateFlow(0f)
    override val audioLevel: StateFlow<Float> = _audioLevel.asStateFlow()

    private val _peakLevel = MutableStateFlow(0f)
    override val peakLevel: StateFlow<Float> = _peakLevel.asStateFlow()

    private var audioRecord: AudioRecord? = null
    private var recordingJob: Job? = null
    private var levelMonitoringJob: Job? = null
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var recordedData: ByteArrayOutputStream? = null
    private var currentConfig: RecorderConfig = RecorderConfig()

    private var smoothedLevel = 0f
    private var currentPeak = 0f

    @SuppressLint("MissingPermission")
    override suspend fun start(config: RecorderConfig) {
        if (_status.value == RecorderStatus.Recording) {
            Log.w(TAG, "Recording already in progress")
            return
        }

        try {
            currentConfig = config
            val audioFormat = AudioFormat.ENCODING_PCM_16BIT
            val channelConfig = when (config.channelCount) {
                1 -> AudioFormat.CHANNEL_IN_MONO
                2 -> AudioFormat.CHANNEL_IN_STEREO
                else -> AudioFormat.CHANNEL_IN_MONO
            }

            // Calculate optimal buffer size
            val minBufferSize = AudioRecord.getMinBufferSize(
                config.sampleRate,
                channelConfig,
                audioFormat
            )

            if (minBufferSize == AudioRecord.ERROR || minBufferSize == AudioRecord.ERROR_BAD_VALUE) {
                _status.value = RecorderStatus.Error
                Log.e(TAG, "Invalid buffer size for config: $config")
                return
            }

            // Use a larger buffer for better quality and stability
            val bufferSize = (minBufferSize * 4).coerceAtLeast(8192)

            Log.d(TAG, "Creating AudioRecord with config: $config, bufferSize: $bufferSize")

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                config.sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            ).also { recorder ->
                if (recorder.state != AudioRecord.STATE_INITIALIZED) {
                    _status.value = RecorderStatus.Error
                    Log.e(TAG, "AudioRecord initialization failed for config: $config")
                    recorder.release()
                    return
                }
            }

            recordedData = ByteArrayOutputStream()

            // Reset level monitoring state
            smoothedLevel = 0f
            currentPeak = 0f
            _audioLevel.value = 0f
            _peakLevel.value = 0f

            audioRecord?.startRecording()
            _status.value = RecorderStatus.Recording

            Log.i(TAG, "Recording started successfully with config: $config")

            recordingJob = scope.launch {
                val buffer = ByteArray(bufferSize / 4) // Smaller read chunks for responsiveness
                var totalBytesRecorded = 0

                try {
                    while (_status.value == RecorderStatus.Recording) {
                        val bytesRead = audioRecord?.read(buffer, 0, buffer.size) ?: 0

                        when {
                            bytesRead > 0 -> {
                                synchronized(recordedData!!) {
                                    recordedData?.write(buffer, 0, bytesRead)
                                }
                                totalBytesRecorded += bytesRead

                                updateAudioLevels(buffer, bytesRead)

                                // Log progress less frequently
                                if (totalBytesRecorded % (config.sampleRate * 2) == 0) { // Every ~1 second
                                    Log.d(TAG, "Recording progress: ${totalBytesRecorded / 1024}KB")
                                }
                            }
                            bytesRead == AudioRecord.ERROR_INVALID_OPERATION -> {
                                Log.e(TAG, "Invalid operation during recording")
                                _status.value = RecorderStatus.Error
                                break
                            }
                            bytesRead == AudioRecord.ERROR_BAD_VALUE -> {
                                Log.e(TAG, "Bad value during recording")
                                _status.value = RecorderStatus.Error
                                break
                            }
                            else -> {
                                Log.w(TAG, "Unexpected read result: $bytesRead")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Recording error", e)
                    _status.value = RecorderStatus.Error
                } finally {
                    cleanupRecording()
                    Log.i(TAG, "Recording finished. Total bytes recorded: $totalBytesRecorded")
                }
            }

        } catch (e: Exception) {
            Log.e(TAG, "Failed to start recording", e)
            _status.value = RecorderStatus.Error
            cleanupRecording()
        }
    }

    override suspend fun stop(): ByteArray = withContext(Dispatchers.IO) {
        if (_status.value != RecorderStatus.Recording) {
            Log.w(TAG, "Not currently recording")
            return@withContext ByteArray(0)
        }

        _status.value = RecorderStatus.Stopped

        try {
            recordingJob?.join() // Wait for recording job to complete

            val pcmData = synchronized(recordedData ?: ByteArrayOutputStream()) {
                recordedData?.toByteArray() ?: ByteArray(0)
            }

            Log.i(TAG, "Recording stopped. PCM data size: ${pcmData.size} bytes")

            if (pcmData.isEmpty()) {
                Log.w(TAG, "No audio data recorded")
                return@withContext ByteArray(0)
            }

            return@withContext if (currentConfig.wrapAsWav) {
                try {
                    val wavData = WavEncoder.pcm16ToWavMono(pcmData, currentConfig.sampleRate)
                    Log.i(TAG, "Generated WAV file: ${wavData.size} bytes (header + data)")
                    wavData
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to create WAV wrapper", e)
                    pcmData // Fall back to raw PCM
                }
            } else {
                pcmData
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error stopping recording", e)
            _status.value = RecorderStatus.Error
            return@withContext ByteArray(0)
        } finally {
            cleanup()
        }
    }

    private fun updateAudioLevels(buffer: ByteArray, bytesRead: Int) {
        try {
            val actualData = ByteArray(bytesRead)
            System.arraycopy(buffer, 0, actualData, 0, bytesRead)

            val currentLevel = AudioLevelCalculator.calculateRMSFromBytes(actualData)

            // Apply smoothing to reduce jitter
            smoothedLevel = AudioLevelCalculator.smoothLevel(currentLevel, smoothedLevel)
            currentPeak = AudioLevelCalculator.updatePeak(smoothedLevel, currentPeak)

            _audioLevel.value = smoothedLevel
            _peakLevel.value = currentPeak

        } catch (e: Exception) {
            Log.w(TAG, "Error calculating audio levels", e)
        }
    }

    override suspend fun cancel() {
        Log.i(TAG, "Cancelling recording")
        _status.value = RecorderStatus.Stopped
        recordingJob?.cancel()
        cleanup()
    }

    private fun cleanupRecording() {
        try {
            audioRecord?.stop()
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping AudioRecord", e)
        }
    }

    private fun cleanup() {
        try {
            audioRecord?.release()
            audioRecord = null
            recordedData?.close()
            recordedData = null

            levelMonitoringJob?.cancel()
            levelMonitoringJob = null

            // Reset levels
            _audioLevel.value = 0f
            _peakLevel.value = 0f
            smoothedLevel = 0f
            currentPeak = 0f

            _status.value = RecorderStatus.Idle
        } catch (e: Exception) {
            Log.w(TAG, "Error during cleanup", e)
        }
    }
}