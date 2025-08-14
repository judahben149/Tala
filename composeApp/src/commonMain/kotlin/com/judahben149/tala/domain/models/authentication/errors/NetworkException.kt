package com.judahben149.tala.domain.models.authentication.errors

sealed class NetworkException(message: String?) : Exception(message) {
    data class BadRequest(val reason: String) : NetworkException(reason)
    data class Unauthorized(val reason: String) : NetworkException(reason)
    data class Forbidden(val reason: String) : NetworkException(reason)
    data class NotFound(val reason: String) : NetworkException(reason)
    data class Timeout(val reason: String) : NetworkException(reason)
    data class ServerError(val reason: String) : NetworkException(reason)
    data class HttpError(val code: Int, val reason: String) : NetworkException(reason)
    data class IO(val reason: String) : NetworkException(reason)
    data class Serialization(val reason: String) : NetworkException(reason)
    data class InvalidResponse(val reason: String) : NetworkException(reason)
    data class Unknown(val reason: String, val throwable: Throwable?) : NetworkException(reason)
}