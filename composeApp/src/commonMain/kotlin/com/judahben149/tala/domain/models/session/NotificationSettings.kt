package com.judahben149.tala.domain.models.session

data class NotificationSettings(
    val notificationsEnabled: Boolean = true,
    val practiceRemindersEnabled: Boolean = true
)