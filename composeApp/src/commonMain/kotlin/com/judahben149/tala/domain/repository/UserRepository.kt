package com.judahben149.tala.domain.repository

import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.session.NotificationSettings
import com.judahben149.tala.domain.models.session.UserProfile
import com.judahben149.tala.domain.models.user.AppUser
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserProfile(): Result<UserProfile, Exception>
    suspend fun updateUserProfile(appUser: AppUser): Result<Unit, Exception>
    suspend fun saveUserProfile(userId: String, profileData: Map<String, Any>): Result<Unit, Exception>
    suspend fun updatePassword(currentPassword: String, newPassword: String): Result<Unit, Exception>
    suspend fun deleteAccount(): Result<Unit, Exception>
    suspend fun updateLearningLanguage(language: String): Result<Unit, Exception>
    suspend fun getLearningLanguage(): Result<String, Exception>
    suspend fun updateNotificationSettings(
        notificationsEnabled: Boolean,
        practiceRemindersEnabled: Boolean
    ): Result<Unit, Exception>
    suspend fun updateOnboardingFlag(hasOnboarded: Boolean): Result<Unit, Exception>
    suspend fun getOnboardingStatusFlag(): Result<Boolean, Exception>
    suspend fun saveLearningLanguage(language: String): Result<Unit, Exception>
    suspend fun saveUserInterests(interests: List<String>): Result<Unit, Exception>
    suspend fun getUserInterests(): Result<List<String>, Exception>
    suspend fun getNotificationSettings(): Result<NotificationSettings, Exception>
    suspend fun getUserData(userId: String): Result<Map<String, Any>, Exception>

    // Local
    suspend fun persistUserData(user: AppUser): Result<Unit, Exception>
    fun observePersistedUser(): Flow<AppUser>
    suspend fun getPersistedUser(): AppUser?
    suspend fun clearPersistedUser(): Result<Unit, Exception>
}
