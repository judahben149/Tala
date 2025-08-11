package com.judahben149.tala.domain.usecases.authentication

import com.judahben149.tala.data.service.firebase.FirebaseAppInfo
import com.judahben149.tala.domain.models.authentication.errors.FirebaseError
import com.judahben149.tala.domain.repository.AuthenticationRepository
import com.judahben149.tala.domain.models.common.Result

class GetCurrentAppUseCase(
    private val authRepository: AuthenticationRepository
) {
    operator fun invoke(): Result<FirebaseAppInfo, FirebaseError> {
        return authRepository.getCurrentApp()
    }
}
