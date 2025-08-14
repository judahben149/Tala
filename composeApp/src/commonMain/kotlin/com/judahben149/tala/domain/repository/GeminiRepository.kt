package com.judahben149.tala.domain.repository

import com.judahben149.tala.domain.models.authentication.errors.NetworkException
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.gemini.Gemini

interface GeminiRepository {
    suspend fun generateText(prompt: String, apiKey: String): Result<Gemini, NetworkException>
    suspend fun generateVision(prompt: String, apiKey: String, images: List<ByteArray>): Result<Gemini, NetworkException>
}