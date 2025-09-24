package com.judahben149.tala.data.service.audio

interface SpeechPlayer {
    suspend fun load(bytes: ByteArray, mimeType: String)
    fun play()
    fun pause()
    fun stop()
    fun isPlaying(): Boolean
    fun getCurrentPosition(): Float // Returns current position in seconds
    fun getDuration(): Float // Returns total duration in seconds
}

expect class AudioPlayerFactory() {
    fun create(): SpeechPlayer
}
