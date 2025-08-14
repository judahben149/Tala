package com.judahben149.tala.domain.usecases.gemini

import com.judahben149.tala.BuildKonfig
import com.judahben149.tala.domain.models.authentication.errors.NetworkException
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.gemini.Gemini
import com.judahben149.tala.domain.repository.GeminiRepository

class GenerateContentUseCase(
    private val geminiRepository: GeminiRepository
) {
    suspend operator fun invoke(content: String, images: List<ByteArray>): Result<Gemini, NetworkException> {
        val apiKey = BuildKonfig.GEMINI_API_KEY
        return if (images.isNotEmpty()) {
            geminiRepository.generateVision(content, apiKey, images)
        } else {
            geminiRepository.generateText(content, apiKey)
        }
    }
}