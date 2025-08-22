package com.judahben149.tala.domain.usecases.speech.recording

import co.touchlab.kermit.Logger
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.repository.AudioRepository
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.math.min
import kotlin.math.round

class StopRecordingUseCase(
    private val audioRepository: AudioRepository,
    private val logger: Logger
) {
    @OptIn(ExperimentalEncodingApi::class)
    suspend operator fun invoke(): Result<Pair<ByteArray, String>, Exception> {
        return try {
            when (val result = audioRepository.stopRecording()) {
                is Result.Success -> {
                    val bytes = result.data
                    if (bytes.isEmpty()) {
                        logger.w { "StopRecordingUseCase: recorded audio is empty (0 bytes)" }
                        Result.Failure(Exception("No audio data recorded"))
                    } else {
                        // Log a non-fatal summary of the WAV (or not) header
                        logWavSummaryCommon(bytes, logger)

                        val base64Audio = Base64.encode(bytes)
                        Result.Success(bytes to base64Audio)
                    }
                }
                is Result.Failure -> {
                    logger.e { "StopRecordingUseCase: failed to stop recording: ${result.error.message}" }
                    Result.Failure(
                        Exception("Failed to stop recording: ${result.error.message}", result.error)
                    )
                }
            }
        } catch (e: Exception) {
            logger.e(e) { "StopRecordingUseCase: error processing recorded audio" }
            Result.Failure(Exception("Error processing recorded audio: ${e.message}", e))
        }
    }
}

/**
- Logs a concise WAV summary if the bytes look like a classic PCM 16-bit WAV.
- If not, logs that fact and a small hex preview.
- KMP/commonMain safe: no Charsets, no String.format.
 */
private fun logWavSummaryCommon(bytes: ByteArray, logger: Logger) {
    logger.d { "WAV bytes size=${bytes.size}" }

    if (bytes.size < 44) {
        logger.w { "WAV check: too small (<44 bytes). Possibly not a WAV container." }
        logHexPreview(bytes, logger)
        return
    }

    fun ascii(from: Int, toExclusive: Int): String {
        val end = min(bytes.size, toExclusive)
        val sb = StringBuilder(end - from)
        var i = from
        while (i < end) {
            val b = bytes[i].toInt() and 0xFF
            // US-ASCII: 0..127 map to same code points; others can be replaced with '.'
            sb.append(if (b in 0..127) b.toChar() else '.')
            i++
        }
        return sb.toString()
    }

    fun leInt(offset: Int): Int {
        if (offset + 4 > bytes.size) return 0
        return (bytes[offset].toInt() and 0xFF) or
                ((bytes[offset + 1].toInt() and 0xFF) shl 8) or
                ((bytes[offset + 2].toInt() and 0xFF) shl 16) or
                ((bytes[offset + 3].toInt() and 0xFF) shl 24)
    }

    fun leShort(offset: Int): Int {
        if (offset + 2 > bytes.size) return 0
        return (bytes[offset].toInt() and 0xFF) or
                ((bytes[offset + 1].toInt() and 0xFF) shl 8)
    }

    // Markers
    val riff = ascii(0, 4)
    val wave = ascii(8, 12)
    val fmt  = ascii(12, 16)

    logger.d { "WAV check: riff='$riff', wave='$wave', fmt='$fmt'" }

    if (riff != "RIFF" || wave != "WAVE" || fmt != "fmt ") {
        logger.w { "WAV check: missing expected markers (RIFF/WAVE/fmt). May not be a WAV file." }
        logHexPreview(bytes, logger)
        return
    }

    val riffChunkSize  = leInt(4)        // total size - 8
    val subchunk1Size  = leInt(16)       // usually 16 for PCM
    val audioFormat    = leShort(20)     // 1 = PCM
    val numChannels    = leShort(22)     // 1 = mono
    val sampleRate     = leInt(24)       // e.g., 16000
    val byteRate       = leInt(28)       // SampleRate * NumChannels * BitsPerSample/8
    val blockAlign     = leShort(32)     // NumChannels * BitsPerSample/8
    val bitsPerSample  = leShort(34)     // e.g., 16

    logger.d {
        "WAV fmt fields: subchunk1Size=$subchunk1Size, audioFormat=$audioFormat, " +
                "numChannels=$numChannels, sampleRate=$sampleRate, byteRate=$byteRate, " +
                "blockAlign=$blockAlign, bitsPerSample=$bitsPerSample"
    }

    // 'data' chunk at conventional offset (36..39 = "data", 40..43 = data size)
    val dataMarker = ascii(36, 40)
    val dataSize   = leInt(40)

    logger.d { "WAV data chunk: marker='$dataMarker', dataSize=$dataSize" }

    // Duration estimate (only a hint)
    if (byteRate > 0 && dataSize > 0) {
        val durationSec = dataSize.toDouble() / byteRate.toDouble()
        logger.d { "WAV duration ~ ${formatTwoDecimals(durationSec)}s" }
    }

    // Consistency notes (non-fatal)
    val expectedTotal = riffChunkSize + 8
    if (expectedTotal != bytes.size) {
        logger.w {
            "WAV note: RIFF size mismatch. header=$expectedTotal, actual=${bytes.size}"
        }
    }
    if (dataMarker != "data") {
        logger.w { "WAV note: 'data' chunk not found at expected position (36..39)." }
    }

    // Hex preview of the first bytes
    logHexPreview(bytes, logger)
}

private fun logHexPreview(bytes: ByteArray, logger: Logger, length: Int = 32) {
    val end = min(bytes.size, length)
    val sb = StringBuilder(end * 3)
    var i = 0
    while (i < end) {
        val b = bytes[i].toInt() and 0xFF
        if (i > 0) sb.append(' ')
        val hex = "0123456789ABCDEF"
        sb.append(hex[b ushr 4])
        sb.append(hex[b and 0x0F])
        i++
    }
    logger.d { "WAV header hex preview: $sb" }
}

/**
 * Simple, KMP-safe two-decimal formatter without String.format.
 */
private fun formatTwoDecimals(value: Double): String {
    val scaled = round(value * 100.0) / 100.0
    // Render with up to 2 decimals, ensuring trailing zero if needed
    val asString = scaled.toString()
    val dot = asString.indexOf('.')
    return if (dot == -1) {
        // no decimals present; add .00
        "$asString.00"
    } else {
        val decimals = asString.length - dot - 1
        when {
            decimals == 0 -> "${asString}00"
            decimals == 1 -> "${asString}0"
            decimals >= 2 -> asString.substring(0, dot + 3)
            else -> asString
        }
    }
}
