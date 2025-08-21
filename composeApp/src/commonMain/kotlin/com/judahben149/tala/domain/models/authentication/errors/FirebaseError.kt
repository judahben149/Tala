package com.judahben149.tala.domain.models.authentication.errors


// Expected exception classes
expect open class FirebaseAuthException(message: String) : Exception

expect class FirebaseAuthInvalidCredentialsException(message: String) : FirebaseAuthException

expect class FirebaseAuthUserCollisionException(message: String) : FirebaseAuthException

expect class FirebaseAuthWeakPasswordException(message: String) : FirebaseAuthException

expect class FirebaseAuthInvalidUserException(message: String) : FirebaseAuthException

expect class FirebaseAuthNetworkException(message: String) : FirebaseAuthException
