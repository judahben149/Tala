package com.judahben149.tala.domain.mappers

import com.judahben149.tala.domain.models.user.AppUser
import dev.gitlive.firebase.auth.FirebaseUser

fun FirebaseUser.toAppUser(): AppUser =
    AppUser(
        userId = uid,
        displayName = displayName ?: "Unknown",
        email = email ?: "Unknown"
    )