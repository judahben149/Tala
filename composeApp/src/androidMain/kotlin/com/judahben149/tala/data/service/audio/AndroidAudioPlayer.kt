package com.judahben149.tala.data.service.audio

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.C.RESULT_END_OF_INPUT
import androidx.media3.common.MediaItem
import androidx.media3.common.MimeTypes
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.ByteArrayDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSource.Factory
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import co.touchlab.kermit.Logger

class AndroidAudioPlayer(
    private val context: Context,
    private val logger: Logger
): SpeechPlayer {
    private var exo: ExoPlayer? = null
    private var lastBytes: ByteArray? = null
    private var lastMime: String = MimeTypes.AUDIO_MPEG

    @OptIn(UnstableApi::class)
    override suspend fun load(bytes: ByteArray, mimeType: String) {
        lastBytes = bytes
        lastMime = mimeType
        val player = exo ?: ExoPlayer.Builder(context).build().also { exo = it }

        val dataSourceFactory = Factory {
            object : DataSource {
                private var opened = false
                private var inner: ByteArrayDataSource? = null
                override fun addTransferListener(transferListener: TransferListener) {}
                override fun open(dataSpec: DataSpec): Long {
                    inner = ByteArrayDataSource(bytes)
                    opened = true
                    return bytes.size.toLong()
                }
                override fun read(buffer: ByteArray, offset: Int, readLength: Int): Int {
                    return if (opened) {
                        val available = bytes.size - inner!!.bytesRead
                        if (available <= 0) RESULT_END_OF_INPUT
                        else {
                            val toRead = minOf(readLength, available)
                            System.arraycopy(bytes, inner!!.bytesRead, buffer, offset, toRead)
                            inner!!.bytesRead += toRead
                            toRead
                        }
                    } else 0
                }
                override fun getUri() = null
                override fun close() { opened = false }
                private var ByteArrayDataSource.bytesRead: Int
                    get() = fieldRef
                    set(value) { fieldRef = value }
                private var fieldRef: Int = 0
            }
        }

        val mediaItem = MediaItem.Builder()
            .setUri("memory://eleven") // dummy
            .setMimeType(mimeType)
            .build()

        val source = ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        player.setMediaSource(source)
        player.prepare()
    }

    override fun play() {
        logger.d { "Media3 is playing now" }
        exo?.play()
    }
    override fun pause() { exo?.pause() }
    override fun stop() { exo?.stop() }
    override fun isPlaying(): Boolean = exo?.isPlaying == true

    @OptIn(UnstableApi::class)
    override fun getCurrentPosition(): Float {
        return (exo?.currentPosition ?: 0L) / 1000f // Convert milliseconds to seconds
    }

    @OptIn(UnstableApi::class)
    override fun getDuration(): Float {
        val durationMs = exo?.duration ?: 0L
        // Handle C.TIME_UNSET (-1) case
        return if (durationMs <= 0) 0f else durationMs / 1000f // Convert milliseconds to seconds
    }
}

actual class AudioPlayerFactory actual constructor() {
    fun create(context: Context, logger: Logger): SpeechPlayer = AndroidAudioPlayer(context, logger)
    actual fun create(): SpeechPlayer {
        throw IllegalStateException("Use create(context) on Android")
    }
}
