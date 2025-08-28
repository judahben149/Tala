package com.judahben149.tala.data.repository

import co.touchlab.kermit.Logger
import com.judahben149.tala.data.local.getCurrentTimeMillis
import com.judahben149.tala.data.service.firebase.FirebaseService
import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.user.Language
import com.judahben149.tala.domain.models.session.UserProfile
import com.judahben149.tala.domain.repository.UserRepository

class UserRepositoryImpl(
    private val firebaseService: FirebaseService,
    private val sessionManager: SessionManager,
    private val logger: Logger
): UserRepository {
    override suspend fun getUserProfile(): Result<UserProfile, Exception> {
        return try {
            val currentUser = firebaseService.getCurrentUser()
                ?: return Result.Failure(Exception("No authenticated user"))

            // Get profile data from Firebase Database
            val profileData = firebaseService.fetchUserProfile(currentUser.userId)

            val userProfile = UserProfile(
                id = currentUser.userId,
                name = currentUser.displayName,
                email = currentUser.email,
                avatarUrl = profileData?.get("avatarUrl") as? String,
                createdAt = profileData?.get("createdAt") as? Long ?: getCurrentTimeMillis(),
                streakDays = (profileData?.get("streakDays") as? Long)?.toInt() ?: 0,
                totalConversations = (profileData?.get("totalConversations") as? Long)?.toInt() ?: 0,
                notificationsEnabled = profileData?.get("notificationsEnabled") as? Boolean ?: true,
                practiceRemindersEnabled = profileData?.get("practiceRemindersEnabled") as? Boolean ?: true
            )

            Result.Success(userProfile)
        } catch (e: Exception) {
            logger.e(e) { "Failed to get user profile" }
            Result.Failure(e)
        }
    }

    override suspend fun updateUserProfile(
        name: String,
        email: String
    ): Result<Unit, Exception> {
        return try {
            val currentUser = firebaseService.getCurrentUser()
                ?: return Result.Failure(Exception("No authenticated user"))

            // Update display name in Firebase Auth
            firebaseService.setDisplayName(name)

            // Update profile data in Firebase Database
            val profileData = mapOf(
                "name" to name,
                "email" to email,
                "updatedAt" to getCurrentTimeMillis()
            )

            firebaseService.updateUserStats(currentUser.userId, profileData)

            Result.Success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "Failed to update user profile" }
            Result.Failure(e)
        }
    }

    override suspend fun saveUserProfile(userId: String, profileData: Map<String, Any>): Result<Unit, Exception> {
        return try {
            firebaseService.saveUserProfile(userId, profileData)
            logger.d { "User profile saved successfully for user: $userId" }
            Result.Success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "Failed to save user profile for user: $userId" }
            Result.Failure(e)
        }
    }

    override suspend fun updatePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit, Exception> {
        return try {
            // This would require Firebase Auth re-authentication
            // Implementation depends on the specific auth flow I went with, will check back
            TODO("Implement password update with re-authentication")
        } catch (e: Exception) {
            logger.e(e) { "Failed to update password" }
            Result.Failure(e)
        }
    }

    override suspend fun deleteAccount(): Result<Unit, Exception> {
        return try {
            val currentUser = firebaseService.getCurrentUser()
                ?: return Result.Failure(Exception("No authenticated user"))

            // Delete user data from database first
            firebaseService.saveUserProfile(currentUser.userId, emptyMap())

            // Delete user data from Firebase Database
            firebaseService.deleteUserData(currentUser.userId)

            // Delete Firebase Auth account
            firebaseService.doDeleteUser()

            Result.Success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "Failed to delete account" }
            Result.Failure(e)
        }
    }

    override suspend fun updateLearningLanguage(language: String): Result<Unit, Exception> {
        return try {
            val currentUser = firebaseService.getCurrentUser()
                ?: return Result.Failure(Exception("No authenticated user"))

            firebaseService.updateUserStats(
                currentUser.userId,
                mapOf("learningLanguage" to language)
            )

            Result.Success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "Failed to update learning language" }
            Result.Failure(e)
        }
    }

    override suspend fun getLearningLanguage(): Result<String, Exception> {
        return try {
            val currentUser = firebaseService.getCurrentUser()
                ?: return Result.Failure(Exception("No authenticated user"))

            val profileData = firebaseService.fetchUserProfile(currentUser.userId)
            val language = profileData?.get("learningLanguage") as? String ?: "Spanish"

            Result.Success(language)
        } catch (e: Exception) {
            logger.e(e) { "Failed to get learning language" }
            Result.Failure(e)
        }
    }

    override suspend fun updateNotificationSettings(
        notificationsEnabled: Boolean,
        practiceRemindersEnabled: Boolean
    ): Result<Unit, Exception> {
        return try {
            val currentUser = firebaseService.getCurrentUser()
                ?: return Result.Failure(Exception("No authenticated user"))

            val settings = mapOf(
                "notificationsEnabled" to notificationsEnabled,
                "practiceRemindersEnabled" to practiceRemindersEnabled
            )

            firebaseService.updateUserStats(currentUser.userId, settings)

            Result.Success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "Failed to update notification settings" }
            Result.Failure(e)
        }
    }

    override suspend fun saveLearningLanguage(language: String): Result<Unit, Exception> {
        return try {
            val userId = firebaseService.getCurrentUser()?.userId
                ?: return Result.Failure(Exception("User not authenticated"))

            val updates = mapOf("learningLanguage" to language)
            firebaseService.updateUserStats(userId, updates)

            // Also save locally
            sessionManager.saveUserLanguagePreference(Language.valueOf(language.uppercase()))

            logger.d { "Learning language saved: $language" }
            Result.Success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "Failed to save learning language" }
            Result.Failure(e)
        }
    }

    override suspend fun saveUserInterests(interests: List<String>): Result<Unit, Exception> {
        return try {
            val userId = firebaseService.getCurrentUser()?.userId
                ?: return Result.Failure(Exception("User not authenticated"))

            val updates = mapOf("interests" to interests)
            firebaseService.updateUserStats(userId, updates)

            logger.d { "User interests saved: $interests" }
            Result.Success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "Failed to save user interests" }
            Result.Failure(e)
        }
    }
}