package com.judahben149.tala.domain.repository

import com.judahben149.tala.data.service.firebase.AppUser
import com.judahben149.tala.data.service.firebase.FirebaseAppInfo
import com.judahben149.tala.domain.models.authentication.errors.FirebaseError
import com.judahben149.tala.domain.models.common.Result


interface AuthenticationRepository {
    fun getCurrentApp(): Result<FirebaseAppInfo, FirebaseError>
    fun getCurrentUser(): Result<AppUser?, FirebaseError>
    suspend fun signIn(email: String, password: String): Result<AppUser, FirebaseError>
    suspend fun createUser(email: String, password: String, displayName: String): Result<AppUser, FirebaseError>
    suspend fun setDisplayName(displayName: String): Result<Unit, FirebaseError>
    suspend fun signOut(): Result<Unit, FirebaseError>
}
