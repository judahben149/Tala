package com.judahben149.tala.data.service.firebase

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.credentials.CredentialManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.judahben149.tala.BuildKonfig
import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthException
import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthInvalidUserException
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.user.AppUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resumeWithException

private var credentialManager: CredentialManager? = null
private var currentActivity: ComponentActivity? = null
private var webClientId: String? = null

actual fun getCurrentFirebaseApp(): FirebaseAppInfo {
    return FirebaseAppInfo(
        projectName = FirebaseApp.getInstance().name,
        projectId = FirebaseApp.getInstance().options.projectId!!
    )
}

actual suspend fun signInWithFirebase(
    email: String,
    password: String
): AppUser {
    val authResult = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
    val user = authResult.user
    if (user != null) {
        return user.toAppUser()
    } else {
        throw FirebaseAuthInvalidUserException("User not found, please sign up")
    }
}

actual suspend fun setFirebaseUserDisplayName(displayName: String) {
    val user = FirebaseAuth.getInstance().currentUser
    if (user != null) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()
        user.updateProfile(profileUpdates).await()
    } else {
        throw FirebaseAuthInvalidUserException("User not found, please sign up")
    }
}

actual suspend fun createFirebaseUser(
    email: String,
    password: String,
    displayName: String
): AppUser {
    val authResult =
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
    val user = authResult.user
    if (user != null) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(displayName)
            .build()
        user.updateProfile(profileUpdates).await()
        return user.toAppUser()
    } else {
        throw FirebaseAuthException("User creation failed, please try again")
    }
}

actual fun getCurrentFirebaseUser(): AppUser? =
    FirebaseAuth.getInstance().currentUser?.toAppUser()

private fun FirebaseUser.toAppUser(): AppUser =
    AppUser(
        userId = uid,
        displayName = displayName ?: "Unknown",
        email = email ?: "Unknown"
    )

actual suspend fun sendPasswordResetEmail(email: String) {
    FirebaseAuth.getInstance().sendPasswordResetEmail(email).await()
}

actual suspend fun deleteUser() {
    val user = FirebaseAuth.getInstance().currentUser
    if (user != null) {
        user.delete().await()
    } else {
        throw FirebaseAuthInvalidUserException("User not found, please sign up")
    }
}



actual suspend fun signOutFirebaseUser() {
    // TODO: Listen to auth state changes, we are assuming the user is signed out here
    FirebaseAuth.getInstance().signOut()
}

actual suspend fun saveFirebaseUserProfile(userId: String, profileData: Map<String, Any>) {
    val database = FirebaseDatabase.getInstance()
    val userRef = database.getReference("users").child(userId)
    userRef.setValue(profileData).await()
}

actual suspend fun fetchFirebaseUserProfile(userId: String): Map<String, Any>? {
    val database = FirebaseDatabase.getInstance()
    val userRef = database.getReference("users").child(userId)
    val snapshot = userRef.get().await()
    return snapshot.value as? Map<String, Any>
}

actual fun observeFirebaseUserProfile(userId: String): Flow<Map<String, Any>?> = callbackFlow {
    val database = FirebaseDatabase.getInstance()
    val userRef = database.getReference("users").child(userId)

    val listener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            trySend(snapshot.value as? Map<String, Any>)
        }

        override fun onCancelled(error: DatabaseError) {
            close(error.toException())
        }
    }

    userRef.addValueEventListener(listener)

    awaitClose {
        userRef.removeEventListener(listener)
    }
}

actual suspend fun updateFirebaseUserStats(userId: String, stats: Map<String, Any>) {
    val database = FirebaseDatabase.getInstance("https://tala-dev-jj-default-rtdb.europe-west1.firebasedatabase.app")
    val userRef = database.getReference("users").child(userId)

    userRef.updateChildren(stats).await()
}

actual suspend fun sendFirebaseEmailVerification() {
    val user = FirebaseAuth.getInstance().currentUser
    user?.sendEmailVerification()?.await()
}

actual suspend fun reloadFirebaseUser() {
    val user = FirebaseAuth.getInstance().currentUser
    user?.reload()?.await()
}

actual fun isFirebaseEmailVerified(): Boolean {
    val user = FirebaseAuth.getInstance().currentUser
    return user?.isEmailVerified ?: false
}

actual suspend fun reauthenticateUser(password: String) {
    if (password.isNotEmpty()) {
        val user = FirebaseAuth.getInstance().currentUser
            ?: throw Exception("No authenticated user")

        val email = user.email
            ?: throw Exception("No email associated with user")

        val credential = EmailAuthProvider.getCredential(email, password)
        user.reauthenticate(credential).await()
    }
}

actual suspend fun refreshFirebaseUserToken(): Boolean {
    val user = FirebaseAuth.getInstance().currentUser ?: return false

    return try {
        user.reload().await()

        user.getIdToken(true).await()
        user.isEmailVerified
    } catch (e: Exception) {
        false
    }
}

actual suspend fun deleteFirebaseUserData(userId: String) {
    val database = FirebaseDatabase.getInstance("https://tala-dev-jj-default-rtdb.europe-west1.firebasedatabase.app")
    val userRef = database.getReference("users").child(userId)

    // Delete all user data from Realtime Database
    userRef.removeValue().await()
}

actual suspend fun getFirebaseUserData(userId: String): Result<Map<String, Any>, Exception> {
    return try {
        val database = FirebaseDatabase.getInstance("https://tala-dev-jj-default-rtdb.europe-west1.firebasedatabase.app")
        val userRef = database.getReference("users").child(userId)

        val snapshot = userRef.get().await()
        if (snapshot.exists()) {
            val userData = snapshot.value as? Map<String, Any> ?: emptyMap()
            Result.Success(userData)
        } else {
            Result.Failure(Exception("User data not found"))
        }
    } catch (e: Exception) {
        Result.Failure(e)
    }
}

//actual fun initializeGoogleSignIn() {
//    // Initialization happens when context is set
//}
//
//fun setCredentialManagerContext(activity: ComponentActivity) {
//    currentActivity = activity
//    credentialManager = CredentialManager.create(activity)
////    webClientId = activity.getString(R.string.default_web_client_id)
//}
//
//private fun generateNonce(): String {
//    val rawNonce = UUID.randomUUID().toString()
//    val bytes = rawNonce.toByteArray()
//    val md = MessageDigest.getInstance("SHA-256")
//    val digest = md.digest(bytes)
//    return digest.fold("") { str, it -> str + "%02x".format(it) }
//}
//
//actual suspend fun signInWithGoogleFirebase(): AppUser {
//    val activity = currentActivity
//    val manager = credentialManager
//    val clientId = webClientId
//
//    if (activity == null || manager == null || clientId == null) {
//        throw FirebaseAuthException(
//            "Google Sign-In not initialized. Ensure setCredentialManagerContext is called."
//        )
//    }
//
//    return try {
//        val hashedNonce = generateNonce()
//
//        val googleIdOption = GetGoogleIdOption.Builder()
//            .setFilterByAuthorizedAccounts(false)
//            .setServerClientId(clientId)
//            .setAutoSelectEnabled(true)
//            .setNonce(hashedNonce)
//            .build()
//
//        val request = GetCredentialRequest.Builder()
//            .addCredentialOption(googleIdOption)
//            .build()
//
//        val result = manager.getCredential(
//            context = activity,
//            request = request
//        )
//
//        when (val credential = result.credential) {
//            is GoogleIdTokenCredential -> {
//                val firebaseCredential = GoogleAuthProvider.getCredential(
//                    credential.idToken,
//                    null
//                )
//
//                val authResult = FirebaseAuth.getInstance()
//                    .signInWithCredential(firebaseCredential)
//                    .await()
//
//                val user = authResult.user
//                    ?: throw FirebaseAuthException("Firebase authentication failed")
//
//                // Save user profile to Firebase Database if new user
//                if (authResult.additionalUserInfo?.isNewUser == true) {
//                    saveInitialUserProfile(user)
//                }
//
//                user.toAppUser()
//            }
//
//            else -> {
//                throw FirebaseAuthException("Unexpected credential type received")
//            }
//        }
//
//    } catch (e: GetCredentialCancellationException) {
//        throw FirebaseAuthException("Google Sign-In was cancelled")
//    } catch (e: NoCredentialException) {
//        throw FirebaseAuthException("No Google accounts available")
//    } catch (e: GetCredentialException) {
//        throw FirebaseAuthException("Google Sign-In failed: ${e.message}")
//    } catch (e: GoogleIdTokenParsingException) {
//        throw FirebaseAuthException("Failed to parse Google ID token: ${e.message}")
//    } catch (e: Exception) {
//        throw FirebaseAuthException("Authentication error: ${e.message}")
//    }
//}
//
//private suspend fun saveInitialUserProfile(user: FirebaseUser) {
//    try {
//        val database = FirebaseDatabase.getInstance()
//        val userRef = database.getReference("users").child(user.uid)
//
//        val profileData = mapOf(
//            "name" to (user.displayName ?: "Unknown"),
//            "email" to (user.email ?: "Unknown"),
//            "avatarUrl" to user.photoUrl?.toString(),
//            "createdAt" to System.currentTimeMillis(),
//            "streakDays" to 0,
//            "totalConversations" to 0,
//            "notificationsEnabled" to true,
//            "practiceRemindersEnabled" to true,
//            "learningLanguage" to "English",
//            "interests" to emptyList<String>()
//        )
//
//        userRef.setValue(profileData).await()
//    } catch (e: Exception) {
//        // Don't fail the sign-in process if profile saving fails
//        android.util.Log.w("GoogleSignIn", "Failed to save initial profile: ${e.message}")
//    }
//}


actual suspend fun fetchFirebaseRemoteConfig(): Map<String, Any> {
    val remoteConfig = FirebaseRemoteConfig.getInstance()

    // Configure Remote Config settings
    val configSettings = FirebaseRemoteConfigSettings.Builder()
        .setMinimumFetchIntervalInSeconds(3600) // 1 hour for production, use 0 for testing
        .build()
    remoteConfig.setConfigSettingsAsync(configSettings)

    // Set default values (optional)
    // remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)

    // Fetch and activate
    remoteConfig.fetchAndActivate().await()

    // Return all values as a map
    val allValues = mutableMapOf<String, Any>()
    remoteConfig.all.forEach { (key, value) ->
        allValues[key] = value.asString()
    }

    return allValues
}

actual suspend fun getFirebaseRemoteConfigString(key: String, defaultValue: String): String {
    val remoteConfig = FirebaseRemoteConfig.getInstance()
    return remoteConfig.getString(key).takeIf { it.isNotEmpty() } ?: defaultValue
}

actual suspend fun getFirebaseRemoteConfigBoolean(key: String, defaultValue: Boolean): Boolean {
    val remoteConfig = FirebaseRemoteConfig.getInstance()
    return remoteConfig.getBoolean(key)
}

actual suspend fun getFirebaseRemoteConfigLong(key: String, defaultValue: Long): Long {
    val remoteConfig = FirebaseRemoteConfig.getInstance()
    return remoteConfig.getLong(key)
}

actual suspend fun getFirebaseRemoteConfigDouble(key: String, defaultValue: Double): Double {
    val remoteConfig = FirebaseRemoteConfig.getInstance()
    return remoteConfig.getDouble(key)
}

actual suspend fun signOutFromGoogleImpl() {
    val activity = currentActivity ?: return

    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(BuildKonfig.FIREBASE_WEB_CLIENT)
        .requestEmail()
        .build()

    val googleSignInClient = GoogleSignIn.getClient(activity, gso)

    suspendCancellableCoroutine { continuation ->
        googleSignInClient.signOut()
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    Log.d("GoogleSignIn", "Google sign out successful")
                    continuation.resume(Unit)
                } else {
                    continuation.resumeWithException(
                        Exception("Google sign out failed: ${task.exception?.message}")
                    )
                }
            }

        continuation.invokeOnCancellation {
            // Handle cancellation if needed
        }
    }
}