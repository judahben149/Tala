package com.judahben149.tala.data.model.network.speech

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SpeechToTextResponse(
    @SerialName("text") val text: String
)
