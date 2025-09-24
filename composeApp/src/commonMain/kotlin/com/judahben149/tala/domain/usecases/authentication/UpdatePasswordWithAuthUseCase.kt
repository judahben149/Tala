package com.judahben149.tala.domain.usecases.authentication

import com.judahben149.tala.data.service.firebase.FirebaseService
import com.judahben149.tala.domain.models.common.Result

class UpdatePasswordWithAuthUseCase(
    private val firebaseService: FirebaseService
) {
    suspend operator fun invoke(currentPassword: String, newPassword: String): Result<Unit, Exception> {
        return try {
            // First re-authenticate
            firebaseService.reauthenticateFirebaseUser(currentPassword)
            
            // Then update password
//            firebaseService.updatePassword(newPassword)
            
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}