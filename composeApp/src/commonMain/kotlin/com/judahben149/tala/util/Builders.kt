package com.judahben149.tala.util

import com.judahben149.tala.data.local.getCurrentTimeMillis
import com.judahben149.tala.domain.models.user.AppUser

fun buildProfileDataFromAppUser(user: AppUser): Map<String, Any> {
    return buildMap {
        // Basic User Info
        put("displayName", user.displayName)
        put("email", user.email)
        put("firstName", user.firstName)
        put("lastName", user.lastName)
        put("isPremiumUser", user.isPremiumUser)
        user.avatarUrl?.let { put("profileImageUrl", it) }
        put("emailVerified", user.emailVerified)
        put("updatedAt", getCurrentTimeMillis())
        put("createdAt", user.createdAt)

        // Learning Progress Fields
        put("streakDays", user.streakDays)
        put("totalConversations", user.totalConversations)
        put("learningLanguage", user.learningLanguage)
        put("interests", user.interests)
        put("currentLevel", user.currentLevel)
        put("totalPoints", user.totalPoints)
        put("weeklyGoal", user.weeklyGoal)
        put("achievementBadges", user.achievementBadges)

        // App Preferences
        put("notificationsEnabled", user.notificationsEnabled)
        put("practiceRemindersEnabled", user.practiceRemindersEnabled)
        user.selectedVoiceId?.let { put("selectedVoiceId", it) }
        put("preferredDifficulty", user.preferredDifficulty)
        put("dailyGoalMinutes", user.dailyGoalMinutes)

        // Social Features
        put("friendsCount", user.friendsCount)
        put("isPrivateProfile", user.isPrivateProfile)
        put("bio", user.bio)
        user.location?.let { put("location", it) }
        user.timezone?.let { put("timezone", it) }

        // App Statistics
        put("totalStudyTimeMinutes", user.totalStudyTimeMinutes)
        put("favoriteTopics", user.favoriteTopics)
        put("lastActiveAt", user.lastActiveAt)
        put("loginCount", user.loginCount)
        put("onboardingCompleted", user.onboardingCompleted)
    }
}

fun buildAppUserFromProfileData(
    userId: String,
    profileData: Map<String, Any>
): AppUser {
    return AppUser(
        // Basic User Info
        displayName = profileData["displayName"] as? String ?: "",
        email = profileData["email"] as? String ?: "",
        firstName = profileData["firstName"] as? String ?: "",
        lastName = profileData["lastName"] as? String ?: "",
        isPremiumUser = profileData["isPremiumUser"] as? Boolean ?: false,
        avatarUrl = profileData["profileImageUrl"] as? String,
        emailVerified = profileData["emailVerified"] as? Boolean ?: false,
        updatedAt = profileData["updatedAt"] as? Long ?: 0L,
        createdAt = profileData["createdAt"] as? Long ?: 0L,

        // Learning Progress Fields
        streakDays = profileData["streakDays"] as? Int ?: 0,
        totalConversations = profileData["totalConversations"] as? Int ?: 0,
        learningLanguage = profileData["learningLanguage"] as? String ?: "",
        interests = profileData["interests"] as? List<String> ?: emptyList(),
        currentLevel = profileData["currentLevel"] as? String ?: "",
        totalPoints = profileData["totalPoints"] as? Int ?: 0,
        weeklyGoal = profileData["weeklyGoal"] as? Int ?: 0,
        achievementBadges = profileData["achievementBadges"] as? List<String> ?: emptyList(),

        // App Preferences
        notificationsEnabled = profileData["notificationsEnabled"] as? Boolean ?: true,
        practiceRemindersEnabled = profileData["practiceRemindersEnabled"] as? Boolean ?: true,
        selectedVoiceId = profileData["selectedVoiceId"] as? String,
        preferredDifficulty = profileData["preferredDifficulty"] as? String ?: "Medium",
        dailyGoalMinutes = profileData["dailyGoalMinutes"] as? Int ?: 15,

        // Social Features
        friendsCount = profileData["friendsCount"] as? Int ?: 0,
        isPrivateProfile = profileData["isPrivateProfile"] as? Boolean ?: false,
        bio = profileData["bio"] as? String ?: "",
        location = profileData["location"] as? String,
        timezone = profileData["timezone"] as? String,

        // App Statistics
        totalStudyTimeMinutes = profileData["totalStudyTimeMinutes"] as? Long ?: 0L,
        favoriteTopics = profileData["favoriteTopics"] as? List<String> ?: emptyList(),
        lastActiveAt = profileData["lastActiveAt"] as? Long ?: 0L,
        loginCount = profileData["loginCount"] as? Int ?: 0,
        onboardingCompleted = profileData["onboardingCompleted"] as? Boolean ?: false,

        userId = userId,
    )
}
