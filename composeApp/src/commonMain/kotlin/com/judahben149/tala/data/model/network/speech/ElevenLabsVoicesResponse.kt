package com.judahben149.tala.data.model.network.speech

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ElevenLabsVoicesResponse(
    val voices: List<Voice>,
    @SerialName("has_more")
    val hasMore: Boolean,
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("next_page_token")
    val nextPageToken: String? = null
)

@Serializable
data class Voice(
    @SerialName("voice_id")
    val voiceId: String,
    val name: String,
    val category: String, // "generated", "cloned", "professional", etc.
    val description: String? = null,
    @SerialName("preview_url")
    val previewUrl: String? = null,
    val labels: Map<String, String>? = null, // Contains gender and other metadata
    val settings: VoiceSettings? = null,
    @SerialName("available_for_tiers")
    val availableForTiers: List<String>? = null,
    val sharing: SharingInfo? = null,
    @SerialName("high_quality_base_model_ids")
    val highQualityBaseModelIds: List<String>? = null,
    @SerialName("safety_control")
    val safetyControl: String? = null, // "NONE", "CAPTCHA_REQUIRED", etc.
    @SerialName("is_owner")
    val isOwner: Boolean? = null,
    @SerialName("is_legacy")
    val isLegacy: Boolean? = null,
    @SerialName("created_at_unix")
    val createdAtUnix: Long? = null
)

@Serializable
data class VoiceSettings(
    val stability: Float? = null,
    @SerialName("similarity_boost")
    val similarityBoost: Float? = null,
    val style: Float? = null,
    @SerialName("use_speaker_boost")
    val useSpeakerBoost: Boolean? = null,
    val speed: Float? = null
)

@Serializable
data class SharingInfo(
    val status: String, // "enabled", "disabled"
    @SerialName("public_owner_id")
    val publicOwnerId: String? = null,
    @SerialName("financial_rewards_enabled")
    val financialRewardsEnabled: Boolean? = null,
    @SerialName("free_users_allowed")
    val freeUsersAllowed: Boolean? = null,
    val featured: Boolean? = null,
    @SerialName("liked_by_count")
    val likedByCount: Int? = null,
    @SerialName("cloned_by_count")
    val clonedByCount: Int? = null,
    @SerialName("image_url")
    val imageUrl: String? = null
)
