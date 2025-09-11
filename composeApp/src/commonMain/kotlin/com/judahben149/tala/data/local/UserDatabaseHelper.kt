package com.judahben149.tala.data.local

import com.judahben149.tala.data.model.UserEntity
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import co.touchlab.kermit.Logger
import com.judahben149.tala.TalaDatabase
import com.judahben149.tala.Users
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json

class UserDatabaseHelper(
    driverFactory: DatabaseDriverFactory,
    private val logger: Logger
) {
    private val database: TalaDatabase = TalaDatabase(driverFactory.createDriver())
    private val userQueries = database.usersQueries

    // Get current user as Flow (reactive)
    fun getCurrentUser(): Flow<UserEntity?> {
        return userQueries.selectCurrentUser()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toUserEntity() }
    }

    // Get current user as one-shot value
    fun getCurrentUserSnapshot(): UserEntity? {
        return userQueries.selectCurrentUser().executeAsOneOrNull()?.toUserEntity()
    }

    suspend fun updateConversationStats(
        userId: String,
        messageQuotaCount: Long,
        totalConversations: Long,
        lastResetDate: String
    ) {
        userQueries.updateConversationStats(
            messageQuotaCount = messageQuotaCount,
            totalConversations = totalConversations,
            messageDailyQuotaCountLastResetDate = lastResetDate,
            id = userId
        )
    }

    fun saveCurrentUser(user: UserEntity) {
        logger.d { "See the user Entity I'm saving oo --> $user" }
        val interestsJson = Json.encodeToString(user.interests)
        val achievementBadgesJson = Json.encodeToString(user.achievementBadges)
        val favoriteTopicsJson = Json.encodeToString(user.favoriteTopics)

        userQueries.insertOrReplaceUser(
            id = user.userId,
            email = user.email,
            isPremiumUser = if (user.isPremiumUser) 1L else 0L,
            signInMethod = user.signInMethod,
            displayName = user.displayName,
            photoUrl = user.avatarUrl,
            isEmailVerified = if (user.isEmailVerified) 1L else 0L,
            firstName = user.firstName,
            lastName = user.lastName,
            phoneNumber = user.phoneNumber,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt,
            streakDays = user.streakDays.toLong(),
            totalConversations = user.totalConversations.toLong(),
            learningLanguage = user.learningLanguage,
            interests = interestsJson,
            currentLevel = user.currentLevel,
            totalPoints = user.totalPoints.toLong(),
            weeklyGoal = user.weeklyGoal.toLong(),
            achievementBadges = achievementBadgesJson,
            notificationsEnabled = if (user.notificationsEnabled) 1L else 0L,
            practiceRemindersEnabled = if (user.practiceRemindersEnabled) 1L else 0L,
            selectedVoiceId = user.selectedVoiceId,
            preferredDifficulty = user.preferredDifficulty,
            dailyGoalMinutes = user.dailyGoalMinutes.toLong(),
            friendsCount = user.friendsCount.toLong(),
            isPrivateProfile = if (user.isPrivateProfile) 1L else 0L,
            bio = user.bio,
            location = user.location,
            timezone = user.timezone,
            totalStudyTimeMinutes = user.totalStudyTimeMinutes,
            favoriteTopics = favoriteTopicsJson,
            lastActiveAt = user.lastActiveAt,
            loginCount = user.loginCount.toLong(),
            messageDailyQuotaCountLastResetDate = user.messageDailyQuotaCountLastResetDate,
            messageQuotaCount = user.messageQuotaCount
        )
    }

    // Clear current user data (logout)
    fun clearCurrentUser() {
        userQueries.deleteAllUsers()
    }
}

// Mapping extension function (same as before)
private fun Users.toUserEntity() = UserEntity(
    userId = id,
    email = email,
    displayName = displayName,
    isPremiumUser = isPremiumUser == 1L,
    signInMethod = signInMethod,
    avatarUrl = photoUrl,
    isEmailVerified = isEmailVerified == 1L,
    firstName = firstName,
    lastName = lastName,
    phoneNumber = phoneNumber,
    createdAt = createdAt,
    updatedAt = updatedAt,
    streakDays = streakDays.toInt(),
    totalConversations = totalConversations.toInt(),
    learningLanguage = learningLanguage,
    interests = interests.let {
        try { Json.decodeFromString<List<String>>(it) } catch (_: Exception) { emptyList() }
    },
    currentLevel = currentLevel,
    totalPoints = totalPoints.toInt(),
    weeklyGoal = weeklyGoal.toInt(),
    achievementBadges = achievementBadges.let {
        try { Json.decodeFromString<List<String>>(it) } catch (_: Exception) { emptyList() }
    },
    notificationsEnabled = notificationsEnabled == 1L,
    practiceRemindersEnabled = practiceRemindersEnabled == 1L,
    selectedVoiceId = selectedVoiceId,
    preferredDifficulty = preferredDifficulty,
    dailyGoalMinutes = dailyGoalMinutes.toInt(),
    friendsCount = friendsCount.toInt(),
    isPrivateProfile = isPrivateProfile == 1L,
    bio = bio,
    location = location,
    timezone = timezone,
    totalStudyTimeMinutes = totalStudyTimeMinutes,
    favoriteTopics = favoriteTopics.let {
        try { Json.decodeFromString<List<String>>(it) } catch (_: Exception) { emptyList() }
    },
    lastActiveAt = lastActiveAt,
    loginCount = loginCount.toInt(),
    messageDailyQuotaCountLastResetDate = messageDailyQuotaCountLastResetDate,
    messageQuotaCount = messageQuotaCount
).also {
    println("PRINTING----> Users: $this")
    println("PRINTING----> UserEntity: $it")
}
