package com.judahben149.tala.domain.repository

import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.session.NotificationSettings
import com.judahben149.tala.domain.models.session.UserProfile

interface UserRepository {
    suspend fun getUserProfile(): Result<UserProfile, Exception>
    suspend fun updateUserProfile(name: String, email: String): Result<Unit, Exception>
    suspend fun saveUserProfile(userId: String, profileData: Map<String, Any>): Result<Unit, Exception>
    suspend fun updatePassword(currentPassword: String, newPassword: String): Result<Unit, Exception>
    suspend fun deleteAccount(): Result<Unit, Exception>
    suspend fun updateLearningLanguage(language: String): Result<Unit, Exception>
    suspend fun getLearningLanguage(): Result<String, Exception>
    suspend fun updateNotificationSettings(
        notificationsEnabled: Boolean,
        practiceRemindersEnabled: Boolean
    ): Result<Unit, Exception>
    suspend fun saveLearningLanguage(language: String): Result<Unit, Exception>
    suspend fun saveUserInterests(interests: List<String>): Result<Unit, Exception>
    suspend fun getNotificationSettings(): Result<NotificationSettings, Exception>
}
