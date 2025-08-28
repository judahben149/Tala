package com.judahben149.tala.util

import com.judahben149.tala.domain.models.user.Language
import com.judahben149.tala.domain.models.user.AppUser
import com.judahben149.tala.domain.models.user.Difficulty
import kotlin.time.ExperimentalTime

object AppUserDefaults {
    val DEFAULT_LEARNING_LANGUAGE = Language.ENGLISH.name
    const val DEFAULT_WEEKLY_GOAL = 7
    const val DEFAULT_DAILY_GOAL_MINUTES = 15
    val DEFAULT_DIFFICULTY = Difficulty.Medium
    
    val DEFAULT_INTERESTS = listOf("Travel", "Culture", "Technology", "Food")
    val DEFAULT_TOPICS = listOf("Greetings", "Shopping", "Travel", "Food")
    
    @OptIn(ExperimentalTime::class)
    fun createNewUser(
        userId: String,
        displayName: String,
        email: String,
        firstName: String = "",
        lastName: String = ""
    ): AppUser {
        val now = kotlin.time.Clock.System.now().toEpochMilliseconds()
        
        return AppUser(
            userId = userId,
            displayName = displayName,
            email = email,
            firstName = firstName,
            lastName = lastName,
            createdAt = now,
            updatedAt = now,
            learningLanguage = DEFAULT_LEARNING_LANGUAGE,
            weeklyGoal = DEFAULT_WEEKLY_GOAL,
            dailyGoalMinutes = DEFAULT_DAILY_GOAL_MINUTES,
            preferredDifficulty = DEFAULT_DIFFICULTY.name
        )
    }
}
