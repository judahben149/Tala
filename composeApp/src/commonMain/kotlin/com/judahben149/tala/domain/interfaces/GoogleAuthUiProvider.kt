package com.judahben149.tala.domain.interfaces

import androidx.compose.runtime.Composable
import com.judahben149.tala.domain.models.authentication.GoogleUser

interface GoogleAuthUiProvider {
    suspend fun signIn(): GoogleUser?
}

interface GoogleAuthProvider {
    @Composable
    fun getUiProvider(): GoogleAuthUiProvider
    suspend fun signOut()
}