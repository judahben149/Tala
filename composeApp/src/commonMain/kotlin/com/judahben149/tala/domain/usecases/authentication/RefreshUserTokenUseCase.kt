package com.judahben149.tala.domain.usecases.authentication

import com.judahben149.tala.data.service.firebase.FirebaseService

class RefreshUserTokenUseCase(
    private val firebaseService: FirebaseService
) {
    suspend operator fun invoke(): Boolean {
        return firebaseService.refreshUserToken()
    }
}