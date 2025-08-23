package com.judahben149.tala.domain.models.analytics

data class WeeklyProgress(
    val conversationsThisWeek: Int,
    val minutesThisWeek: Long,
    val newVocabularyThisWeek: Int,
    val averageDailyMinutes: Double
)