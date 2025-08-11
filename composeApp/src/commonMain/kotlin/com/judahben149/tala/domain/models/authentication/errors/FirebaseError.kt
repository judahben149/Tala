package com.judahben149.tala.domain.models.authentication.errors

sealed class FirebaseError(message: String) : Exception(message)

// Authentication Errors
class InvalidCredentialsError(message: String = "Invalid email or password") : FirebaseError(message)
class UserNotFoundError(message: String = "User account not found") : FirebaseError(message)
class WeakPasswordError(message: String = "Password is too weak") : FirebaseError(message)
class EmailAlreadyInUseError(message: String = "Email address is already registered") : FirebaseError(message)
class InvalidEmailError(message: String = "Invalid email format") : FirebaseError(message)

// Network & Service Errors
class NetworkError(message: String = "Network connection failed") : FirebaseError(message)
class ServiceUnavailableError(message: String = "Firebase service temporarily unavailable") : FirebaseError(message)

// User State Errors
class UserNotSignedInError(message: String = "User must be signed in to perform this action") : FirebaseError(message)
class UserDisabledError(message: String = "User account has been disabled") : FirebaseError(message)

// General Errors
class UnknownFirebaseError(message: String = "An unknown Firebase error occurred") : FirebaseError(message)
