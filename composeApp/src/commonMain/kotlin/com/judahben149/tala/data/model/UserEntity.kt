package com.judahben149.tala.data.model

import kotlinx.serialization.Serializable

@Serializable
data class UserEntity(
    val id: String,
    val email: String,
    val isPremiumUser: Boolean = false,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val isEmailVerified: Boolean = false,

    // Additional Basic Info
    val firstName: String? = null,
    val lastName: String? = null,
    val phoneNumber: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,

    // Learning Progress Fields
    val streakDays: Int = 0,
    val totalConversations: Int = 0,
    val learningLanguage: String = "English",
    val interests: List<String> = emptyList(),
    val currentLevel: String = "Beginner",
    val totalPoints: Int = 0,
    val weeklyGoal: Int = 7,
    val achievementBadges: List<String> = emptyList(),

    // App Preferences
    val notificationsEnabled: Boolean = true,
    val practiceRemindersEnabled: Boolean = true,
    val selectedVoiceId: String? = null,
    val preferredDifficulty: String = "Medium",
    val dailyGoalMinutes: Int = 15,

    // Social Features
    val friendsCount: Int = 0,
    val isPrivateProfile: Boolean = false,
    val bio: String = "",
    val location: String? = null,
    val timezone: String? = null,

    // App Statistics
    val totalStudyTimeMinutes: Long = 0L,
    val favoriteTopics: List<String> = emptyList(),
    val lastActiveAt: Long = 0L,
    val loginCount: Int = 0
)
