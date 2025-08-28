package com.judahben149.tala.data.service.firebase

import com.google.firebase.FirebaseApp
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthException
import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthInvalidUserException
import com.judahben149.tala.domain.models.user.AppUser
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.judahben149.tala.domain.models.common.Result

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
    val user = FirebaseAuth.getInstance().currentUser
        ?: throw Exception("No authenticated user")

    val email = user.email
        ?: throw Exception("No email associated with user")

    val credential = EmailAuthProvider.getCredential(email, password)
    user.reauthenticate(credential).await()
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