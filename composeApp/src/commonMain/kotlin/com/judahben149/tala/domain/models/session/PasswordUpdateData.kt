package com.judahben149.tala.domain.models.session

data class PasswordUpdateData(
    val currentPassword: String,
    val newPassword: String,
    val confirmPassword: String
)