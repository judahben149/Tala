package com.judahben149.tala.domain.usecases.permissions

import com.judahben149.tala.domain.models.common.Result

expect class RequestRecordingPermissionUseCase {
    suspend operator fun invoke(): Result<Boolean, Exception>

}