package com.judahben149.tala.domain.usecases.authentication

import com.judahben149.tala.data.service.firebase.FirebaseAppInfo
import com.judahben149.tala.domain.models.authentication.errors.FirebaseAuthException
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.repository.AuthenticationRepository

class GetCurrentAppUseCase(
    private val authRepository: AuthenticationRepository
) {
    operator fun invoke(): Result<FirebaseAppInfo, FirebaseAuthException> {
        return authRepository.getCurrentApp()
    }
}
