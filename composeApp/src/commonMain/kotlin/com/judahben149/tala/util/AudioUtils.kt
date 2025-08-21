package com.judahben149.tala.util

import kotlin.io.encoding.Base64
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