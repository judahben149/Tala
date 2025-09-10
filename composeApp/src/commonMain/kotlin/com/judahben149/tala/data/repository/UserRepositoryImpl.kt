package com.judahben149.tala.data.repository

import co.touchlab.kermit.Logger
import com.judahben149.tala.data.local.UserDatabaseHelper
import com.judahben149.tala.data.local.getCurrentTimeMillis
import com.judahben149.tala.data.service.firebase.FirebaseService
import com.judahben149.tala.data.service.firebase.getCurrentFirebaseUser
import com.judahben149.tala.domain.managers.SessionManager
import com.judahben149.tala.domain.mappers.toAppUser
import com.judahben149.tala.domain.mappers.toUserEntity
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.session.NotificationSettings
import com.judahben149.tala.domain.models.user.Language
import com.judahben149.tala.domain.models.session.UserProfile
import com.judahben149.tala.domain.models.user.AppUser
import com.judahben149.tala.domain.repository.UserRepository
import com.judahben149.tala.util.buildProfileDataFromAppUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserRepositoryImpl(
    private val firebaseService: FirebaseService,
    private val sessionManager: SessionManager,
    private val userDatabaseHelper: UserDatabaseHelper,
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
        appUser: AppUser
    ): Result<Unit, Exception> {
        return try {
            val currentUser = firebaseService.getCurrentUser()
                ?: return Result.Failure(Exception("No authenticated user"))

            if (appUser.displayName != currentUser.displayName) {
                firebaseService.setDisplayName(appUser.displayName)
            }

            val profileData = buildProfileDataFromAppUser(appUser)
            firebaseService.updateUserStats(appUser.userId, profileData)

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
            firebaseService.signOutFromGoogle()

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

    override suspend fun updateOnboardingFlag(hasOnboarded: Boolean): Result<Unit, Exception> {
        return try {
            val currentUser = firebaseService.getCurrentUser()
                ?: return Result.Failure(Exception("No authenticated user"))

            val updates = mapOf("onboardingCompleted" to hasOnboarded)
            firebaseService.updateUserStats(currentUser.userId, updates)

//            getPersistedUser()?.let {
//                val updatedUser = it.copy(onboardingCompleted = hasOnboarded)
//                persistUserData(updatedUser)
//            }

            sessionManager.markOnboardingCompleted()

            Result.Success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "Failed to update onboarding flag" }
            Result.Failure(e)
        }
    }

    override suspend fun getOnboardingStatusFlag(): Result<Boolean, Exception> {
        return try {
            val persistedUser = getPersistedUser()

            persistedUser?.let { Result.Success(it.hasCompletedOnboarding()) }
                ?: Result.Failure(Exception("No authenticated user"))
        } catch (ex: Exception) {
            logger.e(ex) { "Failed to get onboarding status flag" }
            Result.Failure(ex)
        }
    }

    override suspend fun incrementConversationCount(): Result<Unit, Exception> {
        return try {
            val userId = firebaseService.getCurrentUser()?.userId
                ?: return Result.Failure(Exception("User not authenticated"))

            val persistedUser = getPersistedUser()

            logger.d { "Incrementing conversation count for user: ${persistedUser?.totalConversations}" }

            val updates = mapOf("totalConversations" to (persistedUser?.totalConversations!!.plus(1)))
            logger.d { "Updating total conversations to ${persistedUser.totalConversations.plus(1)}" }

            firebaseService.updateUserStats(userId, updates)

            Result.Success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "Failed to increment conversation count" }
            Result.Failure(e)
        }
    }

    override suspend fun saveLearningLanguage(language: String): Result<Unit, Exception> {
        return try {
            val userId = firebaseService.getCurrentUser()?.userId
                ?: return Result.Failure(Exception("User not authenticated"))

            val persistedUser = getPersistedUser()

            val updates = mapOf("learningLanguage" to language)
            firebaseService.updateUserStats(userId, updates)

            persistedUser?.let {
                val updatedUser = it.copy(learningLanguage = language)
                persistUserData(updatedUser)
            }

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

            val persistedUser = getPersistedUser()

            val updates = mapOf("interests" to interests)
            firebaseService.updateUserStats(userId, updates)

            persistedUser?.let {
                val updatedUser = it.copy(interests = interests)
                persistUserData(updatedUser)
            }

            logger.d { "User interests saved: $interests" }
            Result.Success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "Failed to save user interests" }
            Result.Failure(e)
        }
    }

    override suspend fun getUserInterests(): Result<List<String>, Exception> {
        return try {
            val persistedUser = getPersistedUser()

            persistedUser?.let { Result.Success(it.interests) }
                ?: Result.Failure(Exception("No authenticated user"))
        } catch (e: Exception) {
            Result.Failure(Exception("Unable to fetch user interests"))
        }
    }

    override suspend fun getNotificationSettings(): Result<NotificationSettings, Exception> {
        return try {
            val userId = firebaseService.getCurrentUser()?.userId
                ?: return Result.Failure(Exception("User not authenticated"))

            when (val result = firebaseService.getUserData(userId)) {
                is Result.Success -> {
                    val userData = result.data
                    val settings = NotificationSettings(
                        notificationsEnabled = userData["notificationsEnabled"] as? Boolean ?: true,
                        practiceRemindersEnabled = userData["practiceRemindersEnabled"] as? Boolean ?: true
                    )
                    Result.Success(settings)
                }
                is Result.Failure -> {
                    logger.w { "Failed to fetch notification settings from server, using defaults" }
                    // Return default settings if fetch fails
                    Result.Success(NotificationSettings())
                }
            }
        } catch (e: Exception) {
            logger.e(e) { "Failed to get notification settings" }
            Result.Failure(e)
        }
    }

    override suspend fun getUserData(userId: String): Result<Map<String, Any>, Exception> {
        return firebaseService.getUserData(userId)
    }

    override suspend fun persistUserData(user: AppUser): Result<Unit, Exception> {
        try {
            logger.d { "Persisting user data yoo: $user" }
            userDatabaseHelper.saveCurrentUser(user.toUserEntity())
            return Result.Success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "Failed to persist user data" }
            return Result.Failure(e)
        }
    }

    override fun observePersistedUser(): Flow<AppUser> {
        return userDatabaseHelper.getCurrentUser().map { it!!.toAppUser() }
    }

    override suspend fun getPersistedUser(): AppUser? {
        val appUser = userDatabaseHelper.getCurrentUserSnapshot()?.toAppUser()
        logger.d { "Got persisted user: $appUser" }
        return appUser
    }

    override suspend fun clearPersistedUser(): Result<Unit, Exception> {
        try {
            userDatabaseHelper.clearCurrentUser()
            return Result.Success(Unit)
        } catch (e: Exception) {
            logger.e(e) { "Failed to clear persisted user" }
            return Result.Failure(e)
        }
    }
}