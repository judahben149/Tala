package com.judahben149.tala.data.service.audio

import co.touchlab.kermit.Logger
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes

class IosAudioPlayer(
    private val logger: Logger
): SpeechPlayer {
    private var player: AVAudioPlayer? = null

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun load(bytes: ByteArray, mimeType: String) {
        // Activate audio session
        val session = AVAudioSession.sharedInstance()
        session.setCategory(AVAudioSessionCategoryPlayback, error = null)
        session.setActive(true, error = null)

        val data = bytes.toNSData()
        // Let AVAudioPlayer detect format from data; if needed, supply fileTypeHint (e.g., "mp3")
        player = AVAudioPlayer(data = data, fileTypeHint = null, error = null)
        player?.prepareToPlay()
    }

    override fun play() {
        logger.d { "ios player is playing stuff now" }
        player?.play()
    }
    override fun pause() { player?.pause() }
    override fun stop() { player?.stop() }
    override fun isPlaying(): Boolean = player?.playing == true
}

actual class AudioPlayerFactory actual constructor() {
    fun create(logger: Logger): SpeechPlayer = IosAudioPlayer(logger)

    actual fun create(): SpeechPlayer {
        throw IllegalStateException("Use create(context) on Android")
    }
}

// Helper
//@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
//private fun ByteArray.toNSData(): NSData = memScoped {
//    NSData.create(bytes = this@toNSData.refTo(0) as COpaquePointer?, length = this@toNSData.size.toULong())
//}

@OptIn(ExperimentalForeignApi::class)
fun ByteArray.toNSData(): NSData {
    return this.usePinned { pinned ->
        NSData.dataWithBytes(
            bytes = pinned.addressOf(0),
            length = this.size.toULong()
        )
    }
}