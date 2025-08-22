package com.judahben149.tala.util

import io.ktor.utils.io.core.toByteArray

object WavEncoder {

    /**
     * Converts PCM 16-bit mono audio data to WAV format compatible with Eleven Labs
     * @param pcmData Raw PCM audio bytes (little-endian 16-bit samples)
     * @param sampleRate Sample rate of the audio (e.g., 44100, 22050, 16000)
     * @return WAV formatted byte array
     */
    fun pcm16ToWavMono(pcmData: ByteArray, sampleRate: Int): ByteArray {
        if (pcmData.isEmpty()) throw IllegalArgumentException("PCM data cannot be empty")

        val validPcmData = if (pcmData.size % 2 != 0) pcmData + byteArrayOf(0) else pcmData

        val channels = 1
        val bitsPerSample = 16
        val byteRate = sampleRate * channels * bitsPerSample / 8
        val blockAlign = channels * bitsPerSample / 8
        val dataSize = validPcmData.size

        val header = ByteArray(44)
        var offset = 0

        "RIFF".toByteArray().copyInto(header, offset)
        offset += 4

        // ✅ RIFF chunk size = 36 + Subchunk2Size
        writeLittleEndianInt(header, offset, 36 + dataSize)
        offset += 4

        "WAVE".toByteArray().copyInto(header, offset); offset += 4
        "fmt ".toByteArray().copyInto(header, offset); offset += 4

        writeLittleEndianInt(header, offset, 16); offset += 4 // Subchunk1Size
        writeLittleEndianShort(header, offset, 1); offset += 2 // AudioFormat
        writeLittleEndianShort(header, offset, channels); offset += 2
        writeLittleEndianInt(header, offset, sampleRate); offset += 4
        writeLittleEndianInt(header, offset, byteRate); offset += 4
        writeLittleEndianShort(header, offset, blockAlign); offset += 2
        writeLittleEndianShort(header, offset, bitsPerSample); offset += 2

        "data".toByteArray().copyInto(header, offset); offset += 4
        writeLittleEndianInt(header, offset, dataSize)

        return header + validPcmData
    }


    private fun writeLittleEndianInt(buffer: ByteArray, offset: Int, value: Int) {
        buffer[offset] = (value and 0xFF).toByte()
        buffer[offset + 1] = ((value shr 8) and 0xFF).toByte()
        buffer[offset + 2] = ((value shr 16) and 0xFF).toByte()
        buffer[offset + 3] = ((value shr 24) and 0xFF).toByte()
    }

    private fun writeLittleEndianShort(buffer: ByteArray, offset: Int, value: Int) {
        buffer[offset] = (value and 0xFF).toByte()
        buffer[offset + 1] = ((value shr 8) and 0xFF).toByte()
    }
}