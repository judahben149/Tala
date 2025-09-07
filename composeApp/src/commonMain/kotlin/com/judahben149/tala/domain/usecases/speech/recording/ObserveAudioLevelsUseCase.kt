package com.judahben149.tala.domain.usecases.speech.recording

import com.judahben149.tala.domain.repository.AudioRepository
import kotlinx.coroutines.flow.StateFlow

class ObserveAudioLevelsUseCase(
    private val audioRepository: AudioRepository
) {
    
    fun observeAudioLevel(): StateFlow<Float> = audioRepository.audioLevel
    
    fun observePeakLevel(): StateFlow<Float> = audioRepository.peakLevel
    
    /**
     * Get a processed level suitable for animations
     * - Applies logarithmic scaling for better visual representation
     * - Maps to 0-100 range for easier use in UI
     */
    fun getAnimationLevel(rawLevel: Float): Int {
        if (rawLevel <= 0f) return 0
        
        // Apply logarithmic scaling for better visual representation
        val logLevel = kotlin.math.log10(rawLevel * 9 + 1)
        return (logLevel * 100).coerceIn(0.0F, 100.0F).toInt()
    }
    
    /**
     * Determine if there's significant speech activity
     */
    fun isSpeaking(level: Float, threshold: Float = 0.1f): Boolean {
        return level > threshold
    }
}