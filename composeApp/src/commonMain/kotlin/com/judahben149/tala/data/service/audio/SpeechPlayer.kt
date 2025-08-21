package com.judahben149.tala.data.service.audio

interface SpeechPlayer {
    suspend fun load(bytes: ByteArray, mimeType: String)
    fun play()
    fun pause()
    fun stop()
    fun isPlaying(): Boolean
}

expect class AudioPlayerFactory() {
    fun create(): SpeechPlayer
}