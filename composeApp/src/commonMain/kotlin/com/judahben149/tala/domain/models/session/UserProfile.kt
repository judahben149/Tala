package com.judahben149.tala.domain.models.session

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String? = null,
    val createdAt: Long,
    val streakDays: Int = 0,
    val totalConversations: Int = 0,
    val notificationsEnabled: Boolean = true,
    val practiceRemindersEnabled: Boolean = true
)