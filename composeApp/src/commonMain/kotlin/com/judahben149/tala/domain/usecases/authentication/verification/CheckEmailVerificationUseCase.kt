package com.judahben149.tala.domain.usecases.authentication.verification

import com.judahben149.tala.data.service.firebase.FirebaseService
import com.judahben149.tala.domain.models.common.Result

class CheckEmailVerificationUseCase(
    private val firebaseService: FirebaseService
) {
    suspend operator fun invoke(): Result<Boolean, Exception> {
        return try {
            firebaseService.reloadUser()
            val isVerified = firebaseService.isEmailVerified()
            Result.Success(isVerified)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}