package com.judahben149.tala.domain.usecases.authentication

import com.judahben149.tala.data.service.firebase.FirebaseService
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.repository.UserRepository

class DeleteAccountWithAuthUseCase(
    private val userRepository: UserRepository,
    private val firebaseService: FirebaseService
) {
    suspend operator fun invoke(password: String): Result<Unit, Exception> {
        return try {
            if (password.isNotEmpty()) {
                firebaseService.reauthenticateFirebaseUser(password)
            }

            userRepository.deleteAccount()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Failure(e)
        }
    }
}