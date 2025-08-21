package com.judahben149.tala.domain.models.authentication.errors

import com.google.firebase.auth.FirebaseAuthException as AndroidFirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException as AndroidInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException as AndroidUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException as AndroidWeakPasswordException
import com.google.firebase.auth.FirebaseAuthInvalidUserException as AndroidInvalidUserException
import com.google.firebase.FirebaseNetworkException as AndroidNetworkException


actual open class FirebaseAuthException actual constructor(message: String) : Exception(message)

actual class FirebaseAuthInvalidCredentialsException actual constructor(message: String) : FirebaseAuthException(message)

actual class FirebaseAuthUserCollisionException actual constructor(message: String) : FirebaseAuthException(message)

actual class FirebaseAuthWeakPasswordException actual constructor(message: String) : FirebaseAuthException(message)

actual class FirebaseAuthInvalidUserException actual constructor(message: String) : FirebaseAuthException(message)

actual class FirebaseAuthNetworkException actual constructor(message: String) : FirebaseAuthException(message)


// Helper function to map Android Firebase exceptions to common exceptions
fun mapAndroidFirebaseException(exception: Exception): FirebaseAuthException {
    return when (exception) {
        is AndroidInvalidCredentialsException ->
            FirebaseAuthInvalidCredentialsException(exception.message ?: "Invalid credentials")
        is AndroidUserCollisionException ->
            FirebaseAuthUserCollisionException(exception.message ?: "User collision")
        is AndroidWeakPasswordException ->
            FirebaseAuthWeakPasswordException(exception.message ?: "Weak password")
        is AndroidInvalidUserException ->
            FirebaseAuthInvalidUserException(exception.message ?: "Invalid user")
        is AndroidNetworkException ->
            FirebaseAuthNetworkException(exception.message ?: "Network error")
        is AndroidFirebaseAuthException ->
            FirebaseAuthException(exception.message ?: "Authentication failed")
        else -> FirebaseAuthException(exception.message ?: "Unknown authentication error")
    }
}