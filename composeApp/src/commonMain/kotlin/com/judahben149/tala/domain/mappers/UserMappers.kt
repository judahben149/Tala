package com.judahben149.tala.domain.mappers

import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.toUpperCase
import com.judahben149.tala.data.model.UserEntity
import com.judahben149.tala.domain.models.authentication.SignInMethod
import com.judahben149.tala.domain.models.conversation.MasteryLevel
import com.judahben149.tala.domain.models.user.AppUser
import com.judahben149.tala.util.diffJson
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

fun AppUser.toUserEntity(): UserEntity = UserEntity(
    userId = userId,
    email = email,
    isPremiumUser = isPremiumUser,
    displayName = displayName,
    avatarUrl = avatarUrl,
    signInMethod = signInMethod.name,
    isEmailVerified = isEmailVerified,
    firstName = firstName,
    lastName = lastName,
    createdAt = createdAt,
    updatedAt = updatedAt,
    streakDays = streakDays,
    totalConversations = totalConversations,
    learningLanguage = learningLanguage,
    interests = interests,
    currentLevel = currentLevel,
    totalPoints = totalPoints,
    weeklyGoal = weeklyGoal,
    achievementBadges = achievementBadges,
    notificationsEnabled = notificationsEnabled,
    practiceRemindersEnabled = practiceRemindersEnabled,
    selectedVoiceId = selectedVoiceId,
    preferredDifficulty = preferredDifficulty,
    dailyGoalMinutes = dailyGoalMinutes,
    friendsCount = friendsCount,
    isPrivateProfile = isPrivateProfile,
    bio = bio,
    location = location,
    timezone = timezone,
    totalStudyTimeMinutes = totalStudyTimeMinutes,
    favoriteTopics = favoriteTopics,
    lastActiveAt = lastActiveAt,
    loginCount = loginCount
).also {
    val json = Json { encodeDefaults = true }
    val userEntityJson = json.encodeToJsonElement(UserEntity.serializer(), it).jsonObject
    val appUserJson = json.encodeToJsonElement(AppUser.serializer(), this).jsonObject

    diffJson("AppUser", appUserJson, "UserEntity", userEntityJson)
}

fun UserEntity.toAppUser(): AppUser = AppUser(
    userId = userId,
    displayName = displayName ?: "",
    email = email,
    isPremiumUser = isPremiumUser,
    signInMethod = SignInMethod.valueOf(signInMethod),
    firstName = firstName ?: "",
    lastName = lastName ?: "",
    avatarUrl = avatarUrl,
    isEmailVerified = isEmailVerified,
    createdAt = createdAt,
    updatedAt = updatedAt,
    streakDays = streakDays,
    totalConversations = totalConversations,
    learningLanguage = learningLanguage,
    interests = interests,
    currentLevel = currentLevel,
    totalPoints = totalPoints,
    weeklyGoal = weeklyGoal,
    achievementBadges = achievementBadges,
    notificationsEnabled = notificationsEnabled,
    practiceRemindersEnabled = practiceRemindersEnabled,
    selectedVoiceId = selectedVoiceId,
    preferredDifficulty = preferredDifficulty,
    dailyGoalMinutes = dailyGoalMinutes,
    friendsCount = friendsCount,
    isPrivateProfile = isPrivateProfile,
    bio = bio,
    location = location,
    timezone = timezone,
    totalStudyTimeMinutes = totalStudyTimeMinutes,
    favoriteTopics = favoriteTopics,
    lastActiveAt = lastActiveAt,
    loginCount = loginCount
).also {
    val json = Json { encodeDefaults = true }
    val userEntityJson = json.encodeToJsonElement(UserEntity.serializer(), this).jsonObject
    val appUserJson = json.encodeToJsonElement(AppUser.serializer(), it).jsonObject

    diffJson("AppUser", appUserJson, "UserEntity", userEntityJson)
}

fun String.toMasteryLevel(): MasteryLevel = MasteryLevel.valueOf(this.uppercase())
