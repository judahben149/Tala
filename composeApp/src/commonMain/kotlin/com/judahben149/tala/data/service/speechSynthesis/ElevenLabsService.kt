package com.judahben149.tala.data.service.speechSynthesis

import com.judahben149.tala.data.model.network.speech.DownloadTtsWithTimestampsResponse
import com.judahben149.tala.data.model.network.speech.ElevenLabsTtsRequest
import com.judahben149.tala.data.model.network.speech.ElevenLabsVoicesResponse
import com.judahben149.tala.data.model.network.speech.SpeechToTextResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse
import de.jensklingenberg.ktorfit.http.Header
import io.ktor.client.request.forms.MultiPartFormDataContent

interface ElevenLabsService {
    
    @POST("v1/text-to-speech/{voice_id}/stream/with-timestamps")
    @Headers("Content-Type: application/json")
    suspend fun streamTextToSpeechWithTimestamps(
        @Path("voice_id") voiceId: String,
        @Query("output_format") outputFormat: String = "mp3_44100_128",
        @Body request: ElevenLabsTtsRequest,
        @Header("xi-api-key") apiKey: String
    ): HttpResponse

    @POST("v1/text-to-speech/{voiceId}/with-timestamps")
    @Headers("Content-Type: application/json")
    suspend fun downloadTextToSpeechWithTimestamps(
        @Path("voiceId") voiceId: String,
        @Query("output_format") outputFormat: String,
        @Header("xi-api-key") apiKey: String,
        @Body request: ElevenLabsTtsRequest
    ): DownloadTtsWithTimestampsResponse

    @POST("v1/speech-to-text")
    @Headers("Accept: application/json")
    suspend fun speechToText(
        @Header("xi-api-key") apiKey: String,
        @Body audioFile: MultiPartFormDataContent,
    ): SpeechToTextResponse
//    ): HttpResponse

    @GET("v2/voices")
    suspend fun getVoices(
        @Header("xi-api-key") apiKey: String,
        @Query("page_token") pageToken: String? = null,
        @Query("page_size") pageSize: Int? = null, // Max 100, defaults to 30
        @Query("category") category: String? = null, // Filter by voice category
        @Query("owner_id") ownerId: String? = null,
        @Query("shared") shared: Boolean? = null,
        @Query("sort") sort: String? = null, // Sort by popularity/trending
        @Query("featured") featured: Boolean? = null, // Get only featured voices
        @Query("search") search: String? = null, // Search term to filter voices
        @Query("gender") gender: String? = null, // Filter by gender (if supported)
        @Query("language") language: String? = null, // Filter by language
        @Query("accent") accent: String? = null // Filter by specific accent
    ): ElevenLabsVoicesResponse
}
