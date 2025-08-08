package com.judahben149.tala.domain.models.common

import com.judahben149.tala.domain.models.common.interfaces.AppError

sealed class NetworkError(override val message: String) : AppError {
    data class Connection(val reason: String) : NetworkError(reason)
}