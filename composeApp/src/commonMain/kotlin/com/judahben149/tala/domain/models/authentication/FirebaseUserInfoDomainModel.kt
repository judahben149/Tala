package com.judahben149.tala.domain.models.authentication

data class FirebaseUserInfoDomainModel(
    val uid: String?,
    val email: String?,
    val providers: List<String>?
)
