package com.judahben149.tala.data.service.gemini

import com.judahben149.tala.data.model.network.GeminiResponseDto
import com.judahben149.tala.data.model.network.RequestBody
import com.judahben149.tala.util.GEMINI_PRO
import com.judahben149.tala.util.GEMINI_PRO_VISION
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query

interface GeminiService {

    // Text-only generation
    @POST("v1beta/models/$GEMINI_PRO:generateContent")
    @Headers("Content-Type: application/json")
    suspend fun generateText(
        @Query("key") apiKey: String,
        @Body body: RequestBody
    ): GeminiResponseDto


    // Text + Image (Vision) generation
    @POST("v1beta/models/$GEMINI_PRO_VISION:generateContent")
    @Headers("Content-Type: application/json")
    suspend fun generateVision(
        @Query("key") apiKey: String,
        @Body body: RequestBody
    ): GeminiResponseDto
}