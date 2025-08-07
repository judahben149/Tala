package com.judahben149.tala.domain.model

data class User(
    val id: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean = false
)
