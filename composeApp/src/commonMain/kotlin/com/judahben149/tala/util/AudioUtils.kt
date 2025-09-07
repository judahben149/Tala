package com.judahben149.tala.util

import kotlin.io.encoding.Base64
import kotlin.math.*
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun decodeBase64Audio(base64: String): ByteArray = Base64.decode(base64)


fun mimeTypeForOutputFormat(outputFormat: String): String = when {
    outputFormat.startsWith("mp3") -> "audio/mpeg"
    outputFormat.startsWith("wav") -> "audio/wav"
    outputFormat.startsWith("pcm") -> "audio/raw"
    outputFormat.startsWith("ulaw") || outputFormat.contains("mulaw", ignoreCase = true) -> "audio/basic"
    else -> "audio/mpeg"
}

object AudioLevelCalculator {

    /**
     * Calculate RMS (Root Mean Square) amplitude from PCM samples
     * Returns normalized value between 0.0 and 1.0
     */
    fun calculateRMS(samples: FloatArray): Float {
        if (samples.isEmpty()) return 0f

        val sumSquares = samples.fold(0.0) { acc, sample ->
            acc + sample * sample
        }
        val rms = sqrt(sumSquares / samples.size)

        // Normalize to 0-1 range (adjust multiplier based on your needs)
        return (rms * 3.0f).coerceIn(0.0, 1.0).toFloat()
    }

    /**
     * Calculate RMS from 16-bit PCM byte array
     */
    fun calculateRMSFromBytes(bytes: ByteArray): Float {
        if (bytes.size < 2) return 0f

        val samples = FloatArray(bytes.size / 2)
        for (i in samples.indices) {
            val sample = (bytes[i * 2].toInt() and 0xFF) or
                    ((bytes[i * 2 + 1].toInt() and 0xFF) shl 8)
            samples[i] = sample.toShort().toFloat() / Short.MAX_VALUE
        }

        return calculateRMS(samples)
    }

    /**
     * Simple peak detector with decay
     */
    fun updatePeak(currentLevel: Float, previousPeak: Float, decayRate: Float = 0.95f): Float {
        return if (currentLevel > previousPeak) {
            currentLevel
        } else {
            previousPeak * decayRate
        }
    }

    /**
     * Apply smoothing to reduce jitter in level readings
     */
    fun smoothLevel(currentLevel: Float, previousLevel: Float, smoothing: Float = 0.3f): Float {
        return previousLevel * (1f - smoothing) + currentLevel * smoothing
    }
}