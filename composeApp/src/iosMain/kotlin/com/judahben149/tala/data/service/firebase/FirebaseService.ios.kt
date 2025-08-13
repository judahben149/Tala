package com.judahben149.tala.data.service.firebase

import cocoapods.FirebaseAuth.FIRAuth
import cocoapods.FirebaseAuth.FIRUser
import cocoapods.FirebaseCore.FIRApp
import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthException
import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthInvalidUserException
import com.judahben149.tala.domain.models.authentication.errors.mapIOSFirebaseError
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSError
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@OptIn(ExperimentalForeignApi::class)
actual fun getCurrentFirebaseApp(): FirebaseAppInfo {
    return FirebaseAppInfo(
        projectName = FIRApp.defaultApp()!!.name,
        projectId = FIRApp.defaultApp()!!.options.projectID()!!
    )
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun signInWithFirebase(
    email: String,
    password: String
): AppUser = suspendCancellableCoroutine { continuation ->
    FIRAuth.auth()
        .signInWithEmail(email = email, password = password, completion = { authResult, error ->
            if (error != null) {
                continuation.resumeWithException(mapIOSFirebaseError(error))
            } else {
                val user = authResult?.user()
                if (user != null) {
                    continuation.resume(user.toAppUser())
                } else {
                    continuation.resumeWithException(FirebaseAuthInvalidUserException("User not found, please sign up"))
                }
            }
        })
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun setFirebaseUserDisplayName(displayName: String) =
    suspendCancellableCoroutine { continuation ->
        val user = FIRAuth.auth().currentUser()
        if (user != null) {
            val changeRequest = user.profileChangeRequest()
            changeRequest.setDisplayName(displayName)
            changeRequest.commitChangesWithCompletion { error ->
                if (error != null) {
                    continuation.resumeWithException(Exception(error.localizedDescription))
                } else {
                    continuation.resume(Unit)
                }
            }
        } else {
            continuation.resumeWithException(FirebaseAuthInvalidUserException("User not found, please sign up"))
        }
    }

@OptIn(ExperimentalForeignApi::class)
actual suspend fun createFirebaseUser(
    email: String,
    password: String,
    displayName: String
): AppUser = suspendCancellableCoroutine { continuation ->
    FIRAuth.auth()
        .createUserWithEmail(email = email, password = password, completion = { authResult, error ->
            if (error != null) {
                continuation.resumeWithException(Exception(error.localizedDescription))
            } else {
                val user = authResult?.user()
                if (user != null) {
                    val changeRequest = user.profileChangeRequest()
                    changeRequest.setDisplayName(displayName)
                    changeRequest.commitChangesWithCompletion { error ->
                        if (error != null) {
                            continuation.resumeWithException(mapIOSFirebaseError(error))
                        } else {
                            continuation.resume(user.toAppUser())
                        }
                    }
                } else {
                    continuation.resumeWithException(FirebaseAuthInvalidUserException("User not found, please sign up"))
                }
            }
        })
}

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual suspend fun signOutFirebaseUser() = suspendCancellableCoroutine { continuation ->
    memScoped {
        val errorPointer: CPointer<ObjCObjectVar<NSError?>> = alloc<ObjCObjectVar<NSError?>>().ptr
        val wasSuccessful = FIRAuth.auth().signOut(errorPointer)
        if (!wasSuccessful) {
            val error = errorPointer.pointed.value
            if (error != null) {
                continuation.resumeWithException(mapIOSFirebaseError(error))
            } else {
                continuation.resumeWithException(FirebaseAuthException("Unknown error occurred during sign out"))
            }
        } else {
            continuation.resume(Unit)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun sendPasswordResetEmail(email: String) =
    suspendCancellableCoroutine { continuation ->
        FIRAuth.auth().sendPasswordResetWithEmail(email = email, completion = { error ->
            if (error != null) {
                continuation.resumeWithException(mapIOSFirebaseError(error))
            } else {
                continuation.resume(Unit)
            }
        })
    }


@OptIn(ExperimentalForeignApi::class)
actual suspend fun deleteUser() =
    suspendCancellableCoroutine { continuation ->
        val user = FIRAuth.auth().currentUser()
        if (user != null) {
            user.deleteWithCompletion { error ->
                if (error != null) {
                    continuation.resumeWithException(mapIOSFirebaseError(error))
                } else {
                    continuation.resume(Unit)
                }
            }
        } else {
            continuation.resumeWithException(
                FirebaseAuthInvalidUserException("No user is currently signed in")
            )
        }
    }


@OptIn(ExperimentalForeignApi::class)
actual fun getCurrentFirebaseUser(): AppUser? =
    FIRAuth.auth().currentUser()?.toAppUser()

@OptIn(ExperimentalForeignApi::class)
private fun FIRUser.toAppUser(): AppUser =
    AppUser(
        userId = uid(),
        displayName = displayName() ?: "Unknown",
        email = email() ?: "Unknown"
    )