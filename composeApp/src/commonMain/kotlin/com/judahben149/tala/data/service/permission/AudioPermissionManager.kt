package com.judahben149.tala.data.service.permission

import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.usecases.permissions.CheckRecordingPermissionUseCase
import com.judahben149.tala.domain.usecases.permissions.RequestRecordingPermissionUseCase

class AudioPermissionManager(
    private val checkPermissionUseCase: CheckRecordingPermissionUseCase,
    private val requestPermissionUseCase: RequestRecordingPermissionUseCase
) {
    
    suspend fun ensurePermissionGranted(): Result<Boolean, Exception> {

        return when (val checkResult = checkPermissionUseCase()) {
            is Result.Success -> {
                if (checkResult.data) {
                    Result.Success(true)
                } else {
                    requestPermissionUseCase()
                }
            }
            is Result.Failure -> {
                requestPermissionUseCase()
            }
        }
    }
    
    suspend fun isPermissionGranted(): Result<Boolean, Exception> {
        return checkPermissionUseCase()
    }
}