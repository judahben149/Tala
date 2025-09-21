package com.judahben149.tala.data.model.network.speech

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ElevenLabsTtsRequest(
    val text: String,
    @SerialName("model_id")
    val modelId: String = "eleven_multilingual_v1",
    @SerialName("voice_settings")
    val voiceSettings: VoiceSettings? = null,
    @SerialName("pronunciation_dict_locators")
    val pronunciationDictLocators: List<PronunciationDictLocator>? = null,
    val seed: Long? = null,
    @SerialName("previous_text")
    val previousText: String? = null,
    @SerialName("next_text")
    val nextText: String? = null,
    @SerialName("previous_request_ids")
    val previousRequestIds: List<String>? = null,
    @SerialName("next_request_ids")
    val nextRequestIds: List<String>? = null,
    @SerialName("text_normalization")
    val textNormalization: String? = null, // "auto", "on", or "off"
    @SerialName("use_pvc_voice")
    val usePvcVoice: Boolean? = null,
    @SerialName("enable_logging")
    val enableLogging: Boolean? = null,
    @SerialName("optimize_streaming_latency")
    val optimizeStreamingLatency: Int? = null, // 0-4 for latency optimization levels
    @SerialName("language_code")
    val languageCode: String? = null // ISO 639-1 language code
)

@Serializable
data class PronunciationDictLocator(
    val id: String,
    @SerialName("version_id")
    val versionId: String
)
