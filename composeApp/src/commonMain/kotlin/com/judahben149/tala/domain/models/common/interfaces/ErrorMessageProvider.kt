package com.judahben149.tala.domain.models.common.interfaces

interface ErrorMessageProvider {
    suspend fun getMessage(error: AppError): String
}
