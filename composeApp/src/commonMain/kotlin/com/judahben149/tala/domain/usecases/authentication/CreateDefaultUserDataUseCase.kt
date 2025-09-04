package com.judahben149.tala.domain.usecases.authentication

import co.touchlab.kermit.Logger
import com.judahben149.tala.data.service.firebase.FirebaseService
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.speech.Gender
import com.judahben149.tala.domain.models.user.AppUser
import com.judahben149.tala.domain.usecases.user.PersistUserDataUseCase
import com.judahben149.tala.util.AvatarUrlGenerator
import com.judahben149.tala.util.buildAppUserFromProfileData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class CreateDefaultUserDataUseCase(
    private val firebaseService: FirebaseService,
    private val persistUserDataUseCase: PersistUserDataUseCase,
    private val logger: Logger
) {
    suspend operator fun invoke(
        user: AppUser,
        isFederatedSignIn: Boolean = false
    ): Result<Map<String, Any>, Exception> {
        return withContext(Dispatchers.IO) {
            try {
                val profileData = buildMap {
                    // Basic User Info
                    put("userId", user.userId)
                    put("displayName", user.displayName)
                    put("email", user.email)
                    put("firstName", user.displayName.split(" ").firstOrNull() ?: "")
                    put(
                        "lastName",
                        user.displayName.split(" ").drop(1).joinToString(" ")
                            .ifEmpty { user.lastName }
                    )
                    user.avatarUrl?.let { put("profileImageUrl", it) }
                    put("avatarUrl", AvatarUrlGenerator.generate(Gender.entries.random()))
                    put("isPremiumUser", user.isPremiumUser)
                    put("emailVerified", if (isFederatedSignIn) true else user.emailVerified)
                    put("createdAt", user.createdAt)
                    put("updatedAt", user.updatedAt)

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

                logger.d { "Profile Data: $profileData" }

                firebaseService.saveUserProfile(user.userId, profileData)

                persistUserDataUseCase(buildAppUserFromProfileData(user.userId, profileData))
                Result.Success(profileData)
            } catch (e: Exception) {
                Result.Failure(e)
            }
        }
    }
}