package com.judahben149.tala.domain.models.common

sealed class Result<out D, out E> {
    data class Success<out D>(val data: D) :
        Result<D, Nothing>()

    data class Failure<E>(val error: E) :
        Result<Nothing, E>()

    data object Loading : Result<Nothing, Nothing>()
}