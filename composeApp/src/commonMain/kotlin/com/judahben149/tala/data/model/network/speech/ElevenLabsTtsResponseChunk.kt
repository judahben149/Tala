package com.judahben149.tala.data.model.network.speech

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ElevenLabsTtsResponseChunk(
    @SerialName("audio_base64")
    val audioBase64: String,
    @SerialName("timestamps_raw")
    val timestampsRaw: List<TimestampInfo>,
    @SerialName("timestamps_normalized")
    val timestampsNormalized: List<TimestampInfo>
)

@Serializable
data class TimestampInfo(
    val character: String,
    @SerialName("start_time_s")
    val startTimeS: Double,
    @SerialName("end_time_s")
    val endTimeS: Double
)
