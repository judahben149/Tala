package com.judahben149.tala.domain.models.authentication.errors

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSError


actual open class FirebaseAuthException actual constructor(message: String) : Exception(message)

actual class FirebaseAuthInvalidCredentialsException actual constructor(message: String) : FirebaseAuthException(message)

actual class FirebaseAuthUserCollisionException actual constructor(message: String) : FirebaseAuthException(message)

actual class FirebaseAuthWeakPasswordException actual constructor(message: String) : FirebaseAuthException(message)

actual class FirebaseAuthInvalidUserException actual constructor(message: String) : FirebaseAuthException(message)

actual class FirebaseAuthNetworkException actual constructor(message: String) : FirebaseAuthException(message)

// Helper function to map iOS Firebase error codes to common exceptions
@OptIn(ExperimentalForeignApi::class)
fun mapIOSFirebaseError(error: NSError): FirebaseAuthException {
    return when (error.code) {
        17009L -> // ERROR_INVALID_CREDENTIAL
            FirebaseAuthInvalidCredentialsException(error.localizedDescription)
        17011L -> // ERROR_WRONG_PASSWORD
            FirebaseAuthInvalidCredentialsException(error.localizedDescription)
        17008L -> // ERROR_INVALID_EMAIL
            FirebaseAuthInvalidCredentialsException(error.localizedDescription)
        17007L -> // ERROR_EMAIL_ALREADY_IN_USE
            FirebaseAuthUserCollisionException(error.localizedDescription)
        17026L -> // ERROR_WEAK_PASSWORD
            FirebaseAuthWeakPasswordException(error.localizedDescription)
        17011L -> // ERROR_USER_NOT_FOUND
            FirebaseAuthInvalidUserException(error.localizedDescription)
        17005L -> // ERROR_USER_DISABLED
            FirebaseAuthInvalidUserException(error.localizedDescription)
        17020L -> // ERROR_NETWORK_ERROR
            FirebaseAuthNetworkException(error.localizedDescription)
        else -> FirebaseAuthException(error.localizedDescription)
    }
}
