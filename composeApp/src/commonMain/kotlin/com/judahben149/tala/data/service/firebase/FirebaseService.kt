package com.judahben149.tala.data.service.firebase

import com.judahben149.tala.domain.models.user.AppUser
import kotlinx.coroutines.flow.Flow
import com.judahben149.tala.domain.models.common.Result

interface FirebaseService {
    fun getCurrentApp(): FirebaseAppInfo
    fun getCurrentUser(): AppUser?
    suspend fun getUserData(userId: String): Result<Map<String, Any>, Exception>
    suspend fun signIn(email: String, password: String): AppUser
    suspend fun setDisplayName(displayName: String)
    suspend fun createUser(email: String, password: String, displayName: String): AppUser
    suspend fun doSendPasswordResetEmail(email: String)
    suspend fun doDeleteUser()
    suspend fun signOut()

    suspend fun saveUserProfile(userId: String, profileData: Map<String, Any>)
    suspend fun fetchUserProfile(userId: String): Map<String, Any>?
    fun observeUserProfile(userId: String): Flow<Map<String, Any>?>
    suspend fun updateUserStats(userId: String, stats: Map<String, Any>)
    suspend fun incrementUserConversationCount(userId: String)
    suspend fun updateStreakDays(userId: String, streakDays: Int)
    suspend fun sendEmailVerification()
    suspend fun reloadUser()
    fun isEmailVerified(): Boolean
    suspend fun reauthenticateFirebaseUser(password: String)
    suspend fun refreshUserToken(): Boolean
    suspend fun deleteUserData(userId: String)
    suspend fun signOutFromGoogle()
    suspend fun fetchRemoteConfig(): Map<String, Any>
    suspend fun getRemoteConfigString(key: String, defaultValue: String = ""): String
    suspend fun getRemoteConfigBoolean(key: String, defaultValue: Boolean = false): Boolean
    suspend fun getRemoteConfigLong(key: String, defaultValue: Long = 0L): Long
    suspend fun getRemoteConfigDouble(key: String, defaultValue: Double = 0.0): Double
}

data class FirebaseAppInfo(
    val projectName: String,
    val projectId: String
) {
    override fun toString(): String =
        "$projectName ($projectId)"
}

class FirebaseServiceImpl : FirebaseService {
    override fun getCurrentApp(): FirebaseAppInfo =
        getCurrentFirebaseApp()

    override suspend fun signIn(email: String, password: String): AppUser =
        signInWithFirebase(
            email = email,
            password = password
        )

    override suspend fun setDisplayName(displayName: String) =
        setFirebaseUserDisplayName(
            displayName = displayName
        )

    override suspend fun createUser(email: String, password: String, displayName: String): AppUser =
        createFirebaseUser(
            email = email,
            password = password,
            displayName = displayName
        )

    override suspend fun doSendPasswordResetEmail(email: String) {
        sendPasswordResetEmail(email)
    }

    override suspend fun doDeleteUser() {
        deleteUser()
    }

    override fun getCurrentUser(): AppUser? =
        getCurrentFirebaseUser()

    override suspend fun getUserData(userId: String): Result<Map<String, Any>, Exception> {
        return getFirebaseUserData(userId)
    }

    override suspend fun signOut() =
        signOutFirebaseUser()

    override suspend fun saveUserProfile(userId: String, profileData: Map<String, Any>) {
        saveFirebaseUserProfile(userId, profileData)
    }

    override suspend fun fetchUserProfile(userId: String): Map<String, Any>? {
        return fetchFirebaseUserProfile(userId)
    }

    override fun observeUserProfile(userId: String): Flow<Map<String, Any>?> {
        return observeFirebaseUserProfile(userId)
    }

    override suspend fun updateUserStats(userId: String, stats: Map<String, Any>) {
        updateFirebaseUserStats(userId, stats)
    }

    override suspend fun incrementUserConversationCount(userId: String) {
        val currentProfile = fetchUserProfile(userId) ?: emptyMap()
        val currentCount = (currentProfile["totalConversations"] as? Long) ?: 0L
        updateUserStats(userId, mapOf("totalConversations" to currentCount + 1))
    }

    override suspend fun updateStreakDays(userId: String, streakDays: Int) {
        updateUserStats(userId, mapOf("streakDays" to streakDays))
    }

    override suspend fun sendEmailVerification() {
        sendFirebaseEmailVerification()
    }

    override suspend fun reloadUser() {
        reloadFirebaseUser()
    }

    override fun isEmailVerified(): Boolean {
        return isFirebaseEmailVerified()
    }

    override suspend fun reauthenticateFirebaseUser(password: String) {
        reauthenticateUser(password)
    }

    override suspend fun refreshUserToken(): Boolean {
        return refreshFirebaseUserToken()
    }

    override suspend fun deleteUserData(userId: String) {
        deleteFirebaseUserData(userId)
    }

    override suspend fun signOutFromGoogle() {
        signOutFromGoogleImpl()
    }

    override suspend fun fetchRemoteConfig(): Map<String, Any> {
        return fetchFirebaseRemoteConfig()
    }

    override suspend fun getRemoteConfigString(key: String, defaultValue: String): String {
        return getFirebaseRemoteConfigString(key, defaultValue)
    }

    override suspend fun getRemoteConfigBoolean(key: String, defaultValue: Boolean): Boolean {
        return getFirebaseRemoteConfigBoolean(key, defaultValue)
    }

    override suspend fun getRemoteConfigLong(key: String, defaultValue: Long): Long {
        return getFirebaseRemoteConfigLong(key, defaultValue)
    }

    override suspend fun getRemoteConfigDouble(key: String, defaultValue: Double): Double {
        return getFirebaseRemoteConfigDouble(key, defaultValue)
    }

//    override suspend fun signInWithGoogle(): AppUser =
//        signInWithGoogleFirebase()
//
//    override suspend fun linkGoogleAccount(): AppUser =
//        linkGoogleAccountFirebase()
}

expect fun getCurrentFirebaseApp(): FirebaseAppInfo
expect fun getCurrentFirebaseUser(): AppUser?
expect suspend fun signOutFirebaseUser()
expect suspend fun signInWithFirebase(email: String, password: String): AppUser
expect suspend fun setFirebaseUserDisplayName(displayName: String)
expect suspend fun createFirebaseUser(email: String, password: String, displayName: String): AppUser
expect suspend fun sendPasswordResetEmail(email: String)

expect suspend fun deleteUser()

expect suspend fun saveFirebaseUserProfile(userId: String, profileData: Map<String, Any>)
expect suspend fun fetchFirebaseUserProfile(userId: String): Map<String, Any>?
expect fun observeFirebaseUserProfile(userId: String): Flow<Map<String, Any>?>
expect suspend fun updateFirebaseUserStats(userId: String, stats: Map<String, Any>)

expect suspend fun sendFirebaseEmailVerification()
expect suspend fun reloadFirebaseUser()
expect fun isFirebaseEmailVerified(): Boolean

expect suspend fun reauthenticateUser(password: String)
expect suspend fun refreshFirebaseUserToken(): Boolean
expect suspend fun deleteFirebaseUserData(userId: String)
expect suspend fun getFirebaseUserData(userId: String): Result<Map<String, Any>, Exception>
expect suspend fun signOutFromGoogleImpl()

expect suspend fun fetchFirebaseRemoteConfig(): Map<String, Any>
expect suspend fun getFirebaseRemoteConfigString(key: String, defaultValue: String): String
expect suspend fun getFirebaseRemoteConfigBoolean(key: String, defaultValue: Boolean): Boolean
expect suspend fun getFirebaseRemoteConfigLong(key: String, defaultValue: Long): Long
expect suspend fun getFirebaseRemoteConfigDouble(key: String, defaultValue: Double): Double

//expect suspend fun signInWithGoogleFirebase(): AppUser
//expect suspend fun linkGoogleAccountFirebase(): AppUser
//expect fun initializeGoogleSignIn()