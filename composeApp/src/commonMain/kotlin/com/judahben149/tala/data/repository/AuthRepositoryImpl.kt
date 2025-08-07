package com.judahben149.tala.data.repository

import com.judahben149.tala.domain.model.User
import com.judahben149.tala.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class AuthRepositoryImpl : AuthRepository {
    private val currentUserFlow = MutableStateFlow<User?>(null)
    
    override suspend fun signInWithGoogle(): Result<User> {
        return try {
            // TODO: Implement Firebase Auth for Android
            // For now, return a mock user
            val mockUser = User(
                id = "google_user_123",
                email = "user@example.com",
                displayName = "Test User",
                photoUrl = null,
                isEmailVerified = true
            )
            currentUserFlow.value = mockUser
            Result.success(mockUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun signInWithApple(): Result<User> {
        return try {
            // TODO: Implement Apple Sign In for iOS
            // For now, return a mock user
            val mockUser = User(
                id = "apple_user_456",
                email = "user@icloud.com",
                displayName = "Apple User",
                photoUrl = null,
                isEmailVerified = true
            )
            currentUserFlow.value = mockUser
            Result.success(mockUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun signOut() {
        currentUserFlow.value = null
    }
    
    override fun getCurrentUser(): Flow<User?> {
        return currentUserFlow
    }
}
