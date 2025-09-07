package com.judahben149.tala.data.service.firebase

import cocoapods.FirebaseAuth.FIRAuth
import cocoapods.FirebaseAuth.FIREmailAuthProvider
import cocoapods.FirebaseAuth.FIRGoogleAuthProvider
import cocoapods.FirebaseAuth.FIRUser
import cocoapods.FirebaseCore.FIRApp
import cocoapods.FirebaseDatabase.*
import cocoapods.FirebaseDatabase.FIRDataEventType.FIRDataEventTypeValue
import cocoapods.GoogleSignIn.GIDConfiguration
import cocoapods.GoogleSignIn.GIDSignIn
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
import platform.Foundation.NSBundle
import platform.Foundation.NSDictionary
import platform.Foundation.NSError
import platform.Foundation.NSNumber
import platform.Foundation.dictionaryWithContentsOfFile
import platform.Foundation.timeIntervalSince1970
import platform.UIKit.UIApplication
import platform.UIKit.UINavigationController
import platform.UIKit.UISceneActivationState
import platform.UIKit.UISplitViewController
import platform.UIKit.UITabBarController
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIWindowScene
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


//@OptIn(ExperimentalForeignApi::class)
//actual fun initializeGoogleSignIn() {
//    val bundle = NSBundle.mainBundle
//    val path = bundle.pathForResource("GoogleService-Info", "plist")
//    if (path != null) {
//        val plist = NSDictionary.dictionaryWithContentsOfFile(path)
//        val clientId = plist?.valueForKey("CLIENT_ID") as? String
//
//        if (clientId != null) {
//            val config = GIDConfiguration(clientID = clientId)
//            GIDSignIn.sharedInstance.configuration = config
//        }
//    }
//}
//
//@OptIn(ExperimentalForeignApi::class)
//actual suspend fun signInWithGoogleFirebase(): AppUser = suspendCancellableCoroutine { continuation ->
//    val presentingViewController = getCurrentViewController()
//
//    if (presentingViewController == null) {
//        continuation.resumeWithException(FirebaseAuthException("No presenting view controller available"))
//        return@suspendCancellableCoroutine
//    }
//
//    GIDSignIn.sharedInstance.signInWithPresentingViewController(presentingViewController) { result, error ->
//        if (error != null) {
//            val errorMessage = when (error.code) {
//                -5L -> "Google Sign-In was cancelled"
//                else -> "Google Sign-In failed: ${error.localizedDescription}"
//            }
//            continuation.resumeWithException(FirebaseAuthException(errorMessage))
//            return@signInWithPresentingViewController
//        }
//
//        val user = result?.user
//        val idToken = user?.idToken?.tokenString
//        val accessToken = user?.accessToken?.tokenString
//
//        if (idToken == null || accessToken == null) {
//            continuation.resumeWithException(FirebaseAuthException("Failed to get Google authentication tokens"))
//            return@signInWithPresentingViewController
//        }
//
//        val credential = FIRGoogleAuthProvider.credentialWithIDToken(idToken, accessToken = accessToken)
//
//        FIRAuth.auth().signInWithCredential(credential) { authResult, authError ->
//            if (authError != null) {
//                continuation.resumeWithException(FirebaseAuthException("Firebase authentication failed: ${authError.localizedDescription}"))
//            } else {
//                val firebaseUser = authResult?.user()
//                if (firebaseUser != null) {
//                    // Save initial profile if new user
//                    val isNewUser = authResult.additionalUserInfo()?.isNewUser() ?: false
//                    if (isNewUser) {
//                        saveInitialUserProfileiOS(firebaseUser)
//                    }
//
//                    continuation.resume(firebaseUser.toAppUser())
//                } else {
//                    continuation.resumeWithException(FirebaseAuthException("Authentication failed - no user returned"))
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalForeignApi::class)
//actual suspend fun linkGoogleAccountFirebase(): AppUser = suspendCancellableCoroutine { continuation ->
//    val currentUser = FIRAuth.auth().currentUser()
//    if (currentUser == null) {
//        continuation.resumeWithException(FirebaseAuthInvalidUserException("No user is currently signed in"))
//        return@suspendCancellableCoroutine
//    }
//
//    val presentingViewController = getCurrentViewController()
//
//    if (presentingViewController == null) {
//        continuation.resumeWithException(FirebaseAuthException("No presenting view controller available"))
//        return@suspendCancellableCoroutine
//    }
//
//    GIDSignIn.sharedInstance.signInWithPresentingViewController(presentingViewController) { result, error ->
//        if (error != null) {
//            continuation.resumeWithException(FirebaseAuthException("Google Sign-In failed: ${error.localizedDescription}"))
//            return@signInWithPresentingViewController
//        }
//
//        val user = result?.user
//        val idToken = user?.idToken?.tokenString
//        val accessToken = user?.accessToken?.tokenString
//
//        if (idToken == null || accessToken == null) {
//            continuation.resumeWithException(FirebaseAuthException("Failed to get Google tokens"))
//            return@signInWithPresentingViewController
//        }
//
//        val credential = FIRGoogleAuthProvider.credentialWithIDToken(idToken, accessToken = accessToken)
//
//        currentUser.linkWithCredential(credential) { authResult, linkError ->
//            if (linkError != null) {
//                continuation.resumeWithException(FirebaseAuthException("Account linking failed: ${linkError.localizedDescription}"))
//            } else {
//                val linkedUser = authResult?.user()
//                if (linkedUser != null) {
//                    continuation.resume(linkedUser.toAppUser())
//                } else {
//                    continuation.resumeWithException(FirebaseAuthException("Account linking failed"))
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalForeignApi::class)
//private fun saveInitialUserProfileiOS(user: FIRUser) {
//    val database = FIRDatabase.database()
//    val userRef = database.reference().child("users").child(user.uid())
//
//    val currentTime = platform.Foundation.NSDate().timeIntervalSince1970 * 1000
//
//    val profileData = mapOf(
//        "name" to (user.displayName() ?: "Unknown"),
//        "email" to (user.email() ?: "Unknown"),
//        "avatarUrl" to (user.photoURL()?.absoluteString ?: ""),
//        "createdAt" to currentTime.toLong(),
//        "streakDays" to 0,
//        "totalConversations" to 0,
//        "notificationsEnabled" to true,
//        "practiceRemindersEnabled" to true,
//        "learningLanguage" to "Spanish",
//        "interests" to emptyList<String>()
//    )
//
//    userRef.setValue(profileData) { error, _ ->
//        if (error != null) {
//            println("Failed to save initial profile: ${error.localizedDescription}")
//        }
//    }
//}
//
//@OptIn(ExperimentalForeignApi::class)
//private fun getCurrentViewController(): UIViewController? {
//    // Try to get the key window from the shared application
//    val application = UIApplication.sharedApplication
//
//    // For iOS 15+ we can use keyWindow, for older versions use the deprecated approach
//    val keyWindow = try {
//        // Try the newer API first
//        application.windows.firstOrNull { window ->
//            (window as UIWindow).isKeyWindow()
//        }
//    } catch (e: Exception) {
//        // Fallback to getting the first window
//        application.windows.firstOrNull()
//    }
//
//    return (keyWindow as UIWindow).rootViewController()?.let { rootVC ->
//        findTopViewController(rootVC)
//    }
//}
//
//@OptIn(ExperimentalForeignApi::class)
//private fun findTopViewController(viewController: UIViewController?): UIViewController? {
//    if (viewController == null) return null
//
//    // If there's a presented view controller, go deeper
//    viewController.presentedViewController?.let {
//        return findTopViewController(it)
//    }
//
//    // Handle different container view controllers
//    return when (viewController) {
//        is UINavigationController -> {
//            findTopViewController(viewController.topViewController)
//        }
//        is UITabBarController -> {
//            findTopViewController(viewController.selectedViewController)
//        }
//        is UISplitViewController -> {
//            if (viewController.viewControllers.isNotEmpty()) {
//                val lastViewController = viewController.viewControllers.lastOrNull() as? UIViewController
//                findTopViewController(lastViewController)
//            } else {
//                viewController
//            }
//        }
//        else -> viewController
//    }
//}
@OptIn(ExperimentalForeignApi::class)
actual suspend fun signOutFromGoogleImpl() {
    GIDSignIn.sharedInstance.signOut()
}