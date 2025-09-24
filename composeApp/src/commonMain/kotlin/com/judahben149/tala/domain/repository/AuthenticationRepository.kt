package com.judahben149.tala.domain.repository

import com.judahben149.tala.data.service.firebase.FirebaseAppInfo
import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthException
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.user.AppUser


interface AuthenticationRepository {
    fun getCurrentApp(): Result<FirebaseAppInfo, FirebaseAuthException>
    fun getCurrentUser(): Result<AppUser?, FirebaseAuthException>
    suspend fun signIn(email: String, password: String): Result<AppUser, FirebaseAuthException>
    suspend fun createUser(email: String, password: String, displayName: String): Result<AppUser, FirebaseAuthException>
    suspend fun setDisplayName(displayName: String): Result<Unit, FirebaseAuthException>
    suspend fun signOut(): Result<Unit, FirebaseAuthException>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit, FirebaseAuthException>
    suspend fun deleteUser(): Result<Unit, FirebaseAuthException>
}
