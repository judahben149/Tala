package com.judahben149.tala.domain.models.common

data class User(
    val id: String,
    val email: String,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val isEmailVerified: Boolean = false
)