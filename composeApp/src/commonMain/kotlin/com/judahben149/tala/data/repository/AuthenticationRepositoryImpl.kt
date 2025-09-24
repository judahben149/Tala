package com.judahben149.tala.data.repository

import com.judahben149.tala.data.service.firebase.FirebaseAppInfo
import com.judahben149.tala.data.service.firebase.FirebaseService
import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthException
import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthInvalidUserException
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.user.AppUser
import com.judahben149.tala.domain.repository.AuthenticationRepository

class AuthenticationRepositoryImpl(
    private val firebaseService: FirebaseService
) : AuthenticationRepository {

    override fun getCurrentApp(): Result<FirebaseAppInfo, FirebaseAuthException> {
        return try {
            val appInfo = firebaseService.getCurrentApp()
            Result.Success(appInfo)
        } catch (e: FirebaseAuthException) {
            Result.Failure(e)
        } catch (e: Exception) {
            Result.Failure(FirebaseAuthInvalidUserException(e.message ?: "Failed to get current app"))
        }
    }

    override fun getCurrentUser(): Result<AppUser?, FirebaseAuthException> {
        return try {
            val user = firebaseService.getCurrentUser()
            Result.Success(user)
        } catch (e: FirebaseAuthException) {
            Result.Failure(e)
        } catch (e: Exception) {
            Result.Failure(FirebaseAuthInvalidUserException(e.message ?: "Failed to get current user"))
        }
    }

    override suspend fun signIn(email: String, password: String): Result<AppUser, FirebaseAuthException> {
        return try {
            val user = firebaseService.signIn(email, password)
            Result.Success(user)
        } catch (e: FirebaseAuthException) {
            Result.Failure(e)
        } catch (_: Exception) {
                Result.Failure(FirebaseAuthException("Incorrect credentials, please try again"))
        }
    }

    override suspend fun createUser(
        email: String,
        password: String,
        displayName: String
    ): Result<AppUser, FirebaseAuthException> {
        return try {
            val user = firebaseService.createUser(email, password, displayName)
            Result.Success(user)
        } catch (e: FirebaseAuthException) {
            Result.Failure(e)
        } catch (e: Exception) {
            Result.Failure(FirebaseAuthException(e.message ?: "User creation failed"))
        }
    }

    override suspend fun setDisplayName(displayName: String): Result<Unit, FirebaseAuthException> {
        return try {
            firebaseService.setDisplayName(displayName)
            Result.Success(Unit)
        } catch (e: FirebaseAuthException) {
            Result.Failure(e)
        } catch (e: FirebaseAuthException) {
            Result.Failure(e)
        } catch (e: Exception) {
            Result.Failure(FirebaseAuthException(e.message ?: "Failed to update display name"))
        }
    }

    override suspend fun signOut(): Result<Unit, FirebaseAuthException> {
        return try {
            firebaseService.signOut()
            Result.Success(Unit)
        } catch (e: FirebaseAuthException) {
            Result.Failure(e)
        } catch (e: Exception) {
            Result.Failure(FirebaseAuthException(e.message ?: "Sign out failed"))
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit, FirebaseAuthException> {
        return try {
            firebaseService.doSendPasswordResetEmail(email)
            Result.Success(Unit)
        } catch (e: FirebaseAuthException) {
            Result.Failure(e)
        } catch (e: Exception) {
            Result.Failure(FirebaseAuthException(e.message ?: "Password reset failed"))
        }
    }

    override suspend fun deleteUser(): Result<Unit, FirebaseAuthException> {
        return try {
            firebaseService.doDeleteUser()
            Result.Success(Unit)
        } catch (e: FirebaseAuthException) {
            Result.Failure(e)
        }
    }
}

