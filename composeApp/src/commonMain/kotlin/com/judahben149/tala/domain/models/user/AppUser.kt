package com.judahben149.tala.domain.models.user

import com.judahben149.tala.domain.models.authentication.SignInMethod
import com.judahben149.tala.domain.models.speech.Gender
import com.judahben149.tala.util.AvatarUrlGenerator
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
data class AppUser(
    val userId: String,
    val displayName: String,
    val email: String,
    val isPremiumUser: Boolean = false,
    val signInMethod: SignInMethod = SignInMethod.EMAIL_PASSWORD,
    val firstName: String = "",
    val lastName: String = "",
    val avatarUrl: String? = AvatarUrlGenerator.generate(Gender.entries.random()),
    val emailVerified: Boolean = false,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L,
    
    // Learning Progress Fields
    val streakDays: Int = 0,
    val totalConversations: Int = 0,
    val learningLanguage: String = Language.ENGLISH.name,
    val interests: List<String> = emptyList(),
    val currentLevel: String = "Beginner", // Beginner, Intermediate, Advanced
    val totalPoints: Int = 0,
    val weeklyGoal: Int = 7, // conversations per week
    val achievementBadges: List<String> = emptyList(),
    
    // App Preferences
    val notificationsEnabled: Boolean = true,
    val practiceRemindersEnabled: Boolean = true,
    val selectedVoiceId: String? = null,
    val preferredDifficulty: String = "Medium", // Easy, Medium, Hard
    val dailyGoalMinutes: Int = 15,
    
    // Social Features (for future use)
    val friendsCount: Int = 0,
    val isPrivateProfile: Boolean = false,
    val bio: String = "",
    val location: String? = null,
    val timezone: String? = null,
    
    // App Statistics
    val totalStudyTimeMinutes: Long = 0L,
    val favoriteTopics: List<String> = emptyList(),
    val lastActiveAt: Long = 0L,
    val loginCount: Int = 0,
    val onboardingCompleted: Boolean = false
) {
    fun greeting(): String {
        val name = firstName.takeIf { it.isNotEmpty() } ?: displayName
        return "Hello, $name! Your email is $email and your user ID is $userId."
    }
    
    fun getFullName(): String {
        return if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
            "$firstName $lastName"
        } else {
            displayName
        }
    }
    
    fun getDisplayInitials(): String {
        return if (firstName.isNotEmpty() && lastName.isNotEmpty()) {
            "${firstName.first().uppercase()}${lastName.first().uppercase()}"
        } else {
            displayName.split(" ").take(2).mapNotNull { it.firstOrNull()?.uppercase() }.joinToString("")
        }
    }
    
    fun getCurrentStreakDescription(): String {
        return when (streakDays) {
            0 -> "Start your learning streak today!"
            1 -> "Great start! Keep it going."
            in 2..6 -> "You're on fire! $streakDays days straight."
            in 7..29 -> "Amazing streak! $streakDays days in a row."
            in 30..99 -> "Incredible! $streakDays day streak!"
            else -> "Legendary! $streakDays days of consistent learning!"
        }
    }
    
    fun getLevelProgress(): Float {
        // Calculate progress within current level based on total conversations
        val conversationsPerLevel = 50
        val currentLevelConversations = totalConversations % conversationsPerLevel
        return currentLevelConversations.toFloat() / conversationsPerLevel.toFloat()
    }
    
    fun getNextLevelConversationsNeeded(): Int {
        val conversationsPerLevel = 50
        val currentLevelConversations = totalConversations % conversationsPerLevel
        return conversationsPerLevel - currentLevelConversations
    }
    
    fun getWeeklyProgress(): Float {
        // This would typically be calculated based on actual weekly data
        // For now, return a sample calculation
        val thisWeekConversations = minOf(totalConversations % 10, weeklyGoal)
        return thisWeekConversations.toFloat() / weeklyGoal.toFloat()
    }
    
    fun hasCompletedOnboarding(): Boolean {
        return onboardingCompleted && 
               learningLanguage.isNotEmpty() && 
               interests.isNotEmpty()
    }
    
    fun canReceiveNotifications(): Boolean {
        return notificationsEnabled && emailVerified
    }
    
    fun getExperienceLevel(): String {
        return when (totalConversations) {
            in 0..9 -> "Newcomer"
            in 10..49 -> "Beginner"
            in 50..149 -> "Intermediate"
            in 150..299 -> "Advanced" 
            in 300..499 -> "Expert"
            else -> "Master"
        }
    }

    fun shouldShowLevelUpNotification(previousConversations: Int): Boolean {
        val conversationsPerLevel = 50
        val currentLevel = totalConversations / conversationsPerLevel
        val previousLevel = previousConversations / conversationsPerLevel
        return currentLevel > previousLevel
    }

    fun getDaysActive(): Int {
        return if (createdAt > 0) {
            val createdDate = kotlin.time.Instant.fromEpochMilliseconds(createdAt)
            val now = kotlin.time.Clock.System.now()
            val duration = now - createdDate
            duration.inWholeDays.toInt()
        } else {
            0
        }
    }


    fun isActiveUser(): Boolean {
        val now = kotlin.time.Clock.System.now()
        val lastActive = kotlin.time.Instant.fromEpochMilliseconds(lastActiveAt)
        val daysSinceActive = (now - lastActive).inWholeDays
        return daysSinceActive <= 7 // Active if used within last week
    }

}
