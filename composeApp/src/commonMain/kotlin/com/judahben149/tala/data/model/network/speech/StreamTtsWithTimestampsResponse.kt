package com.judahben149.tala.data.model.network.speech

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonIgnoreUnknownKeys

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class StreamTtsWithTimestampsResponse(
    @SerialName("audio_base64")
    val audioBase64: String,
    @SerialName("timestamps_raw")
    val timestampsRaw: List<TimestampInfo> = emptyList(),
    @SerialName("timestamps_normalized")
    val timestampsNormalized: List<TimestampInfo> = emptyList()
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonIgnoreUnknownKeys
data class TimestampInfo(
    val character: String,
    @SerialName("start_time_s")
    val startTimeS: Double,
    @SerialName("end_time_s")
    val endTimeS: Double
)
