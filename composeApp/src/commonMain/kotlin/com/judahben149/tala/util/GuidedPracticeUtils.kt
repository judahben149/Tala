package com.judahben149.tala.util

import com.judahben149.tala.domain.models.conversation.GuidedPracticeScenario
import com.judahben149.tala.domain.models.conversation.MasteryLevel

/**
 * Utility class for managing guided practice scenarios
 */
object GuidedPracticeUtils {
    
    /**
     * Gets scenarios appropriate for user's mastery level
     */
    fun getScenariosForLevel(masteryLevel: MasteryLevel): List<GuidedPracticeScenario> {
        return GuidedPracticeScenario.entries.filter { scenario ->
            scenario.difficulty <= masteryLevel
        }.sortedBy { it.difficulty }
    }
    
    /**
     * Gets scenarios by category/theme
     */
    fun getScenariosByTheme(): Map<String, List<GuidedPracticeScenario>> {
        return mapOf(
            "Daily Life" to listOf(
                GuidedPracticeScenario.CASUAL_CONVERSATION,
                GuidedPracticeScenario.SHOPPING_BARGAINING,
                GuidedPracticeScenario.PHONE_CALLS
            ),
            "Travel" to listOf(
                GuidedPracticeScenario.TRAVEL_DIRECTIONS,
                GuidedPracticeScenario.AIRPORT_HOTEL,
                GuidedPracticeScenario.ORDERING_RESTAURANT
            ),
            "Professional" to listOf(
                GuidedPracticeScenario.JOB_INTERVIEW,
                GuidedPracticeScenario.PHONE_CALLS
            ),
            "Social" to listOf(
                GuidedPracticeScenario.MEETING_PEOPLE,
                GuidedPracticeScenario.CASUAL_CONVERSATION
            ),
            "Essential" to listOf(
                GuidedPracticeScenario.DOCTOR_VISIT,
                GuidedPracticeScenario.EMERGENCY_SITUATIONS
            )
        )
    }
    
    /**
     * Recommends next scenario based on completed ones and mastery level
     */
    fun recommendNextScenario(
        completedScenarios: List<String>,
        masteryLevel: MasteryLevel
    ): GuidedPracticeScenario? {
        val availableScenarios = getScenariosForLevel(masteryLevel)
        val uncompletedScenarios = availableScenarios.filter { scenario ->
            scenario.id !in completedScenarios
        }
        
        return when {
            uncompletedScenarios.isEmpty() -> null
            uncompletedScenarios.any { it.difficulty == MasteryLevel.BEGINNER } -> {
                // Prioritize beginner scenarios first
                uncompletedScenarios.first { it.difficulty == MasteryLevel.BEGINNER }
            }
            else -> uncompletedScenarios.first()
        }
    }
    
    /**
     * Formats scenario duration for display
     */
    fun formatDuration(scenario: GuidedPracticeScenario): String {
        return "⏱️ ${scenario.duration}"
    }
    
    /**
     * Gets difficulty badge text
     */
    fun getDifficultyBadge(scenario: GuidedPracticeScenario): String {
        return when (scenario.difficulty) {
            MasteryLevel.BEGINNER -> "👶 Beginner"
            MasteryLevel.NOVICE -> "🌱 Novice" 
            MasteryLevel.INTERMEDIATE -> "📚 Intermediate"
            MasteryLevel.ADVANCED -> "🎓 Advanced"
            MasteryLevel.PROFICIENT -> "⭐ Proficient"
            MasteryLevel.EXPERT -> "🏆 Expert"
        }
    }
}

/**
 * Data class for UI display of guided practice scenarios
 */
data class GuidedPracticeCard(
    val scenario: GuidedPracticeScenario,
    val isUnlocked: Boolean = true,
    val isCompleted: Boolean = false,
    val completionCount: Int = 0,
    val lastCompletedDate: String? = null
) {
    val displayTitle: String get() = scenario.title
    val displayDescription: String get() = scenario.description
    val displayDuration: String get() = scenario.duration
    val difficultyLevel: String get() = scenario.difficulty.name.lowercase().replaceFirstChar { it.uppercase() }
}

/**
 * Extension functions for easier usage
 */
fun MasteryLevel.getMaxWordCount(): Int {
    return when (this) {
        MasteryLevel.BEGINNER -> 8
        MasteryLevel.NOVICE -> 15
        MasteryLevel.INTERMEDIATE -> 25
        MasteryLevel.ADVANCED -> 35
        MasteryLevel.PROFICIENT -> 45
        MasteryLevel.EXPERT -> 50
    }
}

fun MasteryLevel.getDisplayName(): String {
    return when (this) {
        MasteryLevel.BEGINNER -> "Beginner"
        MasteryLevel.NOVICE -> "Novice"
        MasteryLevel.INTERMEDIATE -> "Intermediate"
        MasteryLevel.ADVANCED -> "Advanced"
        MasteryLevel.PROFICIENT -> "Proficient"
        MasteryLevel.EXPERT -> "Expert"
    }
}

fun MasteryLevel.getEmoji(): String {
    return when (this) {
        MasteryLevel.BEGINNER -> "🌱"
        MasteryLevel.NOVICE -> "📖"
        MasteryLevel.INTERMEDIATE -> "📚"
        MasteryLevel.ADVANCED -> "🎓"
        MasteryLevel.PROFICIENT -> "⭐"
        MasteryLevel.EXPERT -> "🏆"
    }
}