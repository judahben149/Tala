package com.judahben149.tala.domain.models.authentication

data class GoogleUser(
    val idToken: String,
    val displayName: String = "",
    val email: String = "",
    val profilePicUrl: String? = null
)