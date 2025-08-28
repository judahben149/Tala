package com.judahben149.tala.data.service.firebase

import cocoapods.FirebaseAuth.FIRAuth
import cocoapods.FirebaseAuth.FIRUser
import cocoapods.FirebaseCore.FIRApp
import cocoapods.FirebaseAuth.FIREmailAuthProvider
import cocoapods.FirebaseDatabase.*
import cocoapods.FirebaseDatabase.FIRDataEventType.FIRDataEventTypeValue
import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthException
import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthInvalidUserException
import com.judahben149.tala.domain.models.authentication.errors.mapIOSFirebaseError
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.user.AppUser
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.ObjCObjectVar
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

@OptIn(ExperimentalForeignApi::class)
actual suspend fun saveFirebaseUserProfile(userId: String, profileData: Map<String, Any>) =
    suspendCancellableCoroutine { continuation ->
        val database = FIRDatabase.database()
        val userRef = database.reference().child("users").child(userId)

        userRef.setValue(profileData) { error, _ ->
            if (error != null) {
                continuation.resumeWithException(Exception(error.localizedDescription))
            } else {
                continuation.resume(Unit)
            }
        }
    }

@OptIn(ExperimentalForeignApi::class)
actual suspend fun fetchFirebaseUserProfile(userId: String): Map<String, Any>? =
    suspendCancellableCoroutine { continuation ->
        val database = FIRDatabase.database()
        val userRef = database.reference().child("users").child(userId)

        userRef.observeSingleEventOfType(
            eventType = FIRDataEventTypeValue,
            withBlock = { snapshot ->
                val value = snapshot?.value() as? Map<String, Any>
                continuation.resume(value)
            },
            withCancelBlock = { error ->
                continuation.resumeWithException(Exception(error?.localizedDescription ?: "Unknown error"))
            }
        )
    }

@OptIn(ExperimentalForeignApi::class)
actual fun observeFirebaseUserProfile(userId: String): Flow<Map<String, Any>?> = callbackFlow {
    val database = FIRDatabase.database()
    val userRef = database.reference().child("users").child(userId)

    val handle = userRef.observeEventType(
        eventType = FIRDataEventTypeValue,
        withBlock = { snapshot ->
            val value = snapshot?.value() as? Map<String, Any>
            trySend(value)
        },
        withCancelBlock = { error ->
            close(Exception(error?.localizedDescription ?: "Unknown error"))
        }
    )

    awaitClose {
        userRef.removeObserverWithHandle(handle)
    }
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun updateFirebaseUserStats(userId: String, stats: Map<String, Any>) =
    suspendCancellableCoroutine { continuation ->
        val database = FIRDatabase.database()
        val userRef = database.reference().child("users").child(userId)

        // First get current data, then update
        userRef.observeSingleEventOfType(
            eventType = FIRDataEventTypeValue,
            withBlock = { snapshot ->
                val currentValue = snapshot?.value() as? Map<String, Any> ?: emptyMap()
                val updatedValue = currentValue.toMutableMap()
                updatedValue.putAll(stats)

                userRef.setValue(updatedValue) { error, _ ->
                    if (error != null) {
                        continuation.resumeWithException(Exception(error.localizedDescription))
                    } else {
                        continuation.resume(Unit)
                    }
                }
            },
            withCancelBlock = { error ->
                continuation.resumeWithException(Exception(error?.localizedDescription ?: "Unknown error"))
            }
        )
    }

@OptIn(ExperimentalForeignApi::class)
actual suspend fun sendFirebaseEmailVerification() = suspendCancellableCoroutine { continuation ->
    val user = FIRAuth.auth().currentUser()
    user?.sendEmailVerificationWithCompletion { error ->
        if (error != null) {
            continuation.resumeWithException(Exception(error.localizedDescription))
        } else {
            continuation.resume(Unit)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun reloadFirebaseUser() = suspendCancellableCoroutine { continuation ->
    val user = FIRAuth.auth().currentUser()
    user?.reloadWithCompletion { error ->
        if (error != null) {
            continuation.resumeWithException(Exception(error.localizedDescription))
        } else {
            continuation.resume(Unit)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
actual fun isFirebaseEmailVerified(): Boolean {
    val user = FIRAuth.auth().currentUser()
    return user?.emailVerified() ?: false
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun reauthenticateUser(password: String) = suspendCancellableCoroutine { continuation ->
    val user = FIRAuth.auth().currentUser()
    if (user == null) {
        continuation.resumeWithException(Exception("No authenticated user"))
        return@suspendCancellableCoroutine
    }

    val email = user.email()
    if (email == null) {
        continuation.resumeWithException(Exception("No email associated with user"))
        return@suspendCancellableCoroutine
    }

    val credential = FIREmailAuthProvider.credentialWithEmail(email = email, password = password)

    user.reauthenticateWithCredential(credential) { authResult, error ->
        if (error != null) {
            continuation.resumeWithException(Exception(error.localizedDescription))
        } else {
            continuation.resume(Unit)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun refreshFirebaseUserToken(): Boolean = suspendCancellableCoroutine { continuation ->
    val user = FIRAuth.auth().currentUser()
    if (user == null) {
        continuation.resume(false)
        return@suspendCancellableCoroutine
    }

    user.reloadWithCompletion { error ->
        if (error != null) {
            continuation.resume(false)
        } else {
            user.getIDTokenForcingRefresh(true) { token, tokenError ->
                if (tokenError == null && token != null) {
                    continuation.resume(user.emailVerified())
                } else {
                    continuation.resume(false)
                }
            }
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun deleteFirebaseUserData(userId: String) = suspendCancellableCoroutine { continuation ->
    val database = FIRDatabase.database().reference()
    val userRef = database.child("users").child(userId)

    userRef.removeValueWithCompletionBlock { error, _ ->
        if (error == null) {
            continuation.resume(Unit)
        } else {
            continuation.resumeWithException(Exception(error.localizedDescription))
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
actual suspend fun getFirebaseUserData(userId: String): Result<Map<String, Any>, Exception> =
    suspendCancellableCoroutine { continuation ->
        val database = FIRDatabase.database().reference()
        val userRef = database.child("users").child(userId)

        userRef.observeSingleEventOfType(FIRDataEventTypeValue,
            withBlock = { snapshot ->
                if (snapshot?.exists() == true) {
                    val userData = snapshot.value() as? Map<String, Any> ?: emptyMap()
                    continuation.resume(Result.Success(userData))
                } else {
                    continuation.resume(Result.Failure(Exception("User data not found")))
                }
            },
            withCancelBlock = { error ->
                continuation.resume(Result.Failure(Exception(error?.localizedDescription ?: "Unknown error")))
            }
        )
    }

