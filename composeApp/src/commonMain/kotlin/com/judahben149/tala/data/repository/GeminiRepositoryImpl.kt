package com.judahben149.tala.data.repository

import co.touchlab.kermit.Logger
import com.judahben149.tala.data.model.network.ContentItem
import com.judahben149.tala.data.model.network.RequestBody
import com.judahben149.tala.data.model.network.RequestInlineData
import com.judahben149.tala.data.model.network.RequestPart
import com.judahben149.tala.data.service.gemini.GeminiService
import com.judahben149.tala.domain.mappers.toGemini // Assuming this maps DTO -> Domain model
import com.judahben149.tala.domain.mappers.toNetworkFailure
import com.judahben149.tala.domain.models.authentication.errors.NetworkException
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.gemini.Gemini
import com.judahben149.tala.domain.repository.GeminiRepository
import io.ktor.util.encodeBase64

class GeminiRepositoryImpl(
    private val service: GeminiService,
    private val logger: Logger
) : GeminiRepository {

    override suspend fun generateText(prompt: String, apiKey: String): Result<Gemini, NetworkException> {
        logger.d { "Generating text with prompt: $prompt" }
        return try {
            val parts = mutableListOf(RequestPart(text = prompt))
            val body = RequestBody(contents = listOf(ContentItem(parts = parts)))
            val dto = service.generateText(apiKey = apiKey, body = body)

            Result.Success(dto.toGemini())
        } catch (t: Throwable) {
            logger.d { "Error generating text: ${t.message}" }
            t.toNetworkFailure()
        }
    }

    override suspend fun generateVision(
        prompt: String,
        apiKey: String,
        images: List<ByteArray>
    ): Result<Gemini, NetworkException> {
        return try {
            val parts = mutableListOf(RequestPart(text = prompt))

            images.forEach { bytes ->
                parts += RequestPart(
                    inlineData = RequestInlineData(
                        mimeType = "image/jpeg",
                        data = bytes.encodeBase64()
                    )
                )
            }

            val body = RequestBody(contents = listOf(ContentItem(parts = parts)))
            val dto = service.generateVision(apiKey = apiKey, body = body)

            Result.Success(dto.toGemini())
        } catch (t: Throwable) {
            t.toNetworkFailure()
        }
    }
}