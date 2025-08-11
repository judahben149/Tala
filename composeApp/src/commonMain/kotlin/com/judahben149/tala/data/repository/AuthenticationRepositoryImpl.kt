package com.judahben149.tala.data.repository

import com.judahben149.tala.data.service.firebase.AppUser
import com.judahben149.tala.data.service.firebase.FirebaseAppInfo
import com.judahben149.tala.data.service.firebase.FirebaseService
import com.judahben149.tala.domain.models.authentication.errors.FirebaseError
import com.judahben149.tala.domain.models.authentication.errors.UnknownFirebaseError
import com.judahben149.tala.domain.models.authentication.errors.UserNotSignedInError
import com.judahben149.tala.domain.repository.AuthenticationRepository
import com.judahben149.tala.domain.models.common.Result


class AuthenticationRepositoryImpl(
    private val firebaseService: FirebaseService
) : AuthenticationRepository {

    override fun getCurrentApp(): Result<FirebaseAppInfo, FirebaseError> {
        return try {
            val appInfo = firebaseService.getCurrentApp()
            Result.Success(appInfo)
        } catch (e: FirebaseError) {
            Result.Failure(e)
        } catch (e: Exception) {
            Result.Failure(UnknownFirebaseError(e.message ?: "Failed to get current app"))
        }
    }

    override fun getCurrentUser(): Result<AppUser?, FirebaseError> {
        return try {
            val user = firebaseService.getCurrentUser()
            Result.Success(user)
        } catch (e: FirebaseError) {
            Result.Failure(e)
        } catch (e: Exception) {
            Result.Failure(UnknownFirebaseError(e.message ?: "Failed to get current user"))
        }
    }

    override suspend fun signIn(email: String, password: String): Result<AppUser, FirebaseError> {
        return try {
            val user = firebaseService.signIn(email, password)
            Result.Success(user)
        } catch (e: FirebaseError) {
            Result.Failure(e)
        } catch (e: Exception) {
            Result.Failure(UnknownFirebaseError(e.message ?: "Sign in failed"))
        }
    }

    override suspend fun createUser(
        email: String,
        password: String,
        displayName: String
    ): Result<AppUser, FirebaseError> {
        return try {
            val user = firebaseService.createUser(email, password, displayName)
            Result.Success(user)
        } catch (e: FirebaseError) {
            Result.Failure(e)
        } catch (e: Exception) {
            Result.Failure(UnknownFirebaseError(e.message ?: "User creation failed"))
        }
    }

    override suspend fun setDisplayName(displayName: String): Result<Unit, FirebaseError> {
        return try {
            firebaseService.setDisplayName(displayName)
            Result.Success(Unit)
        } catch (e: UserNotSignedInError) {
            Result.Failure(e)
        } catch (e: FirebaseError) {
            Result.Failure(e)
        } catch (e: Exception) {
            Result.Failure(UnknownFirebaseError(e.message ?: "Failed to update display name"))
        }
    }

    override suspend fun signOut(): Result<Unit, FirebaseError> {
        return try {
            firebaseService.signOut()
            Result.Success(Unit)
        } catch (e: FirebaseError) {
            Result.Failure(e)
        } catch (e: Exception) {
            Result.Failure(UnknownFirebaseError(e.message ?: "Sign out failed"))
        }
    }
}

