package com.judahben149.tala.domain.repository

import com.judahben149.tala.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun signInWithGoogle(): Result<User>
    suspend fun signInWithApple(): Result<User>
    suspend fun signOut()
    fun getCurrentUser(): Flow<User?>
}
