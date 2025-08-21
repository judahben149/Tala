package com.judahben149.tala.data.repository

import com.judahben149.tala.data.model.network.speech.DownloadTtsWithTimestampsResponse
import com.judahben149.tala.data.model.network.speech.ElevenLabsTtsRequest
import com.judahben149.tala.data.model.network.speech.StreamTtsWithTimestampsResponse
import com.judahben149.tala.data.model.network.speech.VoiceSettings
import com.judahben149.tala.data.service.speechSynthesis.ElevenLabsService
import com.judahben149.tala.domain.mappers.toNetworkFailure
import com.judahben149.tala.domain.models.authentication.errors.NetworkException
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.speech.AudioChunk
import com.judahben149.tala.domain.models.speech.CharacterTimestamp
import com.judahben149.tala.domain.models.speech.SpeechModel
import com.judahben149.tala.domain.repository.ElevenLabsTtsRepository
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsChannel
import io.ktor.util.decodeBase64Bytes
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class TtsRepositoryImpl(
    private val service: ElevenLabsService
) : ElevenLabsTtsRepository {

    override suspend fun streamTextToSpeech(
        text: String,
        voiceId: String,
        apiKey: String,
        model: SpeechModel,
        voiceSettings: VoiceSettings?,
        outputFormat: String
    ): Result<Flow<AudioChunk>, NetworkException> {

        // Input validation
        if (apiKey.isBlank()) {
            return Result.Failure(NetworkException.Unauthorized("Missing API key"))
        }
        if (text.isBlank()) {
            return Result.Failure(NetworkException.BadRequest("Text cannot be blank"))
        }
        if (voiceId.isBlank()) {
            return Result.Failure(NetworkException.BadRequest("Voice ID cannot be blank"))
        }

        val request = ElevenLabsTtsRequest(
            text = text,
            modelId = model.modelId,
            voiceSettings = voiceSettings?.let {
                VoiceSettings(
                    stability = it.stability,
                    similarityBoost = it.similarityBoost,
                    style = it.style,
                    useSpeakerBoost = it.useSpeakerBoost
                )
            }
        )

        return runCatching {
            val response = service.streamTextToSpeechWithTimestamps(
                voiceId = voiceId,
                outputFormat = outputFormat,
                request = request,
                apiKey = apiKey
            )

            // Convert streaming response to Flow
            createAudioChunkFlow(response)
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = { it.toNetworkFailure() }
        )
    }

    override suspend fun downloadTextToSpeech(
        text: String,
        voiceId: String,
        apiKey: String,
        model: SpeechModel,
        voiceSettings: VoiceSettings?,
        outputFormat: String
    ): Result<DownloadTtsWithTimestampsResponse, NetworkException> {

        // Input validation
        if (apiKey.isBlank()) {
            return Result.Failure(NetworkException.Unauthorized("Missing API key"))
        }
        if (text.isBlank()) {
            return Result.Failure(NetworkException.BadRequest("Text cannot be blank"))
        }
        if (voiceId.isBlank()) {
            return Result.Failure(NetworkException.BadRequest("Voice ID cannot be blank"))
        }

        val request = ElevenLabsTtsRequest(
            text = text,
            modelId = model.modelId,
            voiceSettings = voiceSettings?.let {
                VoiceSettings(
                    stability = it.stability,
                    similarityBoost = it.similarityBoost,
                    style = it.style,
                    useSpeakerBoost = it.useSpeakerBoost
                )
            }
        )

        return runCatching {
            service.downloadTextToSpeechWithTimestamps(
                voiceId = voiceId,
                outputFormat = outputFormat,
                apiKey = apiKey,
                request = request
            )
        }.fold(
            onSuccess = { Result.Success(it) },
            onFailure = { it.toNetworkFailure() }
        )
    }

    private suspend fun createAudioChunkFlow(response: HttpResponse): Flow<AudioChunk> = flow {
        try {
            val channel = response.bodyAsChannel()

            while (!channel.isClosedForRead) {
                try {
                    // Read each line from the streaming response
                    val line = channel.readUTF8Line() ?: break

                    if (line.isNotBlank()) {
                        // Parse the JSON chunk
                        val responseChunk = Json.decodeFromString<StreamTtsWithTimestampsResponse>(line)

                        // Convert base64 audio to bytes
                        val audioBytes = responseChunk.audioBase64.decodeBase64Bytes()

                        // Convert timestamps
                        val timestamps = responseChunk.timestampsNormalized.map { timestamp ->
                            CharacterTimestamp(
                                character = timestamp.character,
                                startTimeS = timestamp.startTimeS,
                                endTimeS = timestamp.endTimeS
                            )
                        }

                        val audioChunk = AudioChunk(
                            audioData = audioBytes,
                            timestamps = timestamps
                        )

                        emit(audioChunk)
                    }
                } catch (e: Exception) {
                    // Handle individual chunk errors gracefully
                    println("Error processing audio chunk: ${e.message}")
                }
            }
        } catch (e: Exception) {
            throw e // Re-throw to be handled by outer runCatching
        }
    }
}