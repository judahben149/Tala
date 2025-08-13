package com.judahben149.tala.data.service.gemini

import com.judahben149.tala.data.model.network.GeminiResponseDto

interface GeminiService {
    suspend fun generateContent(content: String, apiKey: String): GeminiResponseDto
    suspend fun generateContentWithImage(content: String,  apiKey: String,images: List<ByteArray> = emptyList()): GeminiResponseDto
}