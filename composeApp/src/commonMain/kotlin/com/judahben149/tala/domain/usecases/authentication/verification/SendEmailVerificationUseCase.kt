package com.judahben149.tala.domain.usecases.authentication.verification

import com.judahben149.tala.data.service.firebase.FirebaseService
import com.judahben149.tala.domain.models.common.Result

class SendEmailVerificationUseCase(
    private val firebaseService: FirebaseService
) {
    suspend operator fun invoke(): Result<Unit, Exception> {
        return try {
            firebaseService.sendEmailVerification()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}