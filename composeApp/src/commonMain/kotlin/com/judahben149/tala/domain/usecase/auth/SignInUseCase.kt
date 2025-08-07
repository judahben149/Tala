package com.judahben149.tala.domain.usecase.auth

import com.judahben149.tala.domain.model.User
import com.judahben149.tala.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class SignInUseCase(
    private val authRepository: AuthRepository
) {
    suspend fun signInWithGoogle(): Result<User> {
        return authRepository.signInWithGoogle()
    }
    
    suspend fun signInWithApple(): Result<User> {
        return authRepository.signInWithApple()
    }
    
    suspend fun signOut() {
        authRepository.signOut()
    }
    
    fun getCurrentUser(): Flow<User?> {
        return authRepository.getCurrentUser()
    }
}
