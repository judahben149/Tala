package com.judahben149.tala.domain.usecases.speech.recording

import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.speech.RecorderConfig

class ValidateAudioConfigUseCase {
    
    operator fun invoke(config: RecorderConfig): Result<RecorderConfig, Exception> {
        return try {
            when {
                config.sampleRate !in VALID_SAMPLE_RATES -> {
                    Result.Failure(Exception("Invalid sample rate: ${config.sampleRate}. Must be one of: ${VALID_SAMPLE_RATES.joinToString()}"))
                }
                
                config.channelCount !in VALID_CHANNEL_COUNTS -> {
                    Result.Failure(Exception("Invalid channel count: ${config.channelCount}. Must be one of: ${VALID_CHANNEL_COUNTS.joinToString()}"))
                }
                
                config.bitsPerSample !in VALID_BITS_PER_SAMPLE -> {
                    Result.Failure(Exception("Invalid bits per sample: ${config.bitsPerSample}. Must be one of: ${VALID_BITS_PER_SAMPLE.joinToString()}"))
                }
                
                else -> Result.Success(config)
            }
        } catch (e: Exception) {
            Result.Failure(Exception("Error validating audio config: ${e.message}", e))
        }
    }
    
    companion object {
        private val VALID_SAMPLE_RATES = listOf(8000, 16000, 22050, 44100, 48000)
        private val VALID_CHANNEL_COUNTS = listOf(1, 2)
        private val VALID_BITS_PER_SAMPLE = listOf(16, 24, 32)
        
        // Optimized config for Eleven Labs STT
        val ELEVEN_LABS_CONFIG = RecorderConfig(
            sampleRate = 16_000,
            channelCount = 1,
            bitsPerSample = 16,
            wrapAsWav = false
        )
        
        // High quality config for general use
        val HIGH_QUALITY_CONFIG = RecorderConfig(
            sampleRate = 44_100,
            channelCount = 2,
            bitsPerSample = 16,
            wrapAsWav = true
        )
    }
}