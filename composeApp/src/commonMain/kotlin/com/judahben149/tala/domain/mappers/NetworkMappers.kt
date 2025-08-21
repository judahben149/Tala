package com.judahben149.tala.domain.mappers

import com.judahben149.tala.domain.models.authentication.errors.NetworkException
import com.judahben149.tala.domain.models.common.Result
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException

class ApiPayloadInvalidException(message: String) : Exception(message)
fun Throwable.toNetworkFailure(): Result.Failure<NetworkException> {
    val mapped = when (this) {
        is ResponseException -> {
            val status = this.response.status.value
            when (status) {
                400 -> NetworkException.BadRequest("Bad request: ${safeMessage(this)}")
                401 -> NetworkException.Unauthorized("Unauthorized: ${safeMessage(this)}")
                403 -> NetworkException.Forbidden("Forbidden: ${safeMessage(this)}")
                404 -> NetworkException.NotFound("Not found: ${safeMessage(this)}")
                408 -> NetworkException.Timeout("Request timeout")
                in 500..599 -> NetworkException.ServerError("Server error($status): ${safeMessage(this)}")
                else -> NetworkException.HttpError(status, "HTTP $status: ${safeMessage(this)}")
            }
        }
        is HttpRequestTimeoutException -> NetworkException.Timeout("Request timed out")
        is ConnectTimeoutException -> NetworkException.Timeout("Connection timed out")
        is SocketTimeoutException -> NetworkException.Timeout("Socket timed out")
        is IOException -> NetworkException.IO("Network I/O error: ${this.message ?: "IO error"}")
        is SerializationException -> NetworkException.Serialization("Serialization error: ${this.message ?: "Invalid JSON"}")
        is ApiPayloadInvalidException -> NetworkException.InvalidResponse(this.message ?: "Invalid response")
        else -> NetworkException.Unknown(this.message ?: "Unknown error", this)
    }
    return Result.Failure(mapped)
}

private fun safeMessage(t: Throwable): String = t.message ?: "An unknown error occurred."
