package com.judahben149.tala.data.repository

import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.session.UserProfile
import com.judahben149.tala.domain.repository.UserRepository

class UserRepositoryImpl(): UserRepository {
    override suspend fun getUserProfile(): Result<UserProfile, Exception> {
        TODO("Not yet implemented")
    }

    override suspend fun updateUserProfile(
        name: String,
        email: String
    ): Result<Unit, Exception> {
        TODO("Not yet implemented")
    }

    override suspend fun updatePassword(
        currentPassword: String,
        newPassword: String
    ): Result<Unit, Exception> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAccount(): Result<Unit, Exception> {
        TODO("Not yet implemented")
    }

    override suspend fun updateLearningLanguage(language: String): Result<Unit, Exception> {
        TODO("Not yet implemented")
    }

    override suspend fun getLearningLanguage(): Result<String, Exception> {
        TODO("Not yet implemented")
    }

    override suspend fun updateNotificationSettings(
        notificationsEnabled: Boolean,
        practiceRemindersEnabled: Boolean
    ): Result<Unit, Exception> {
        TODO("Not yet implemented")
    }
}