package com.judahben149.tala.domain.mappers

import com.judahben149.tala.domain.models.common.Result
suspend fun <Data, Out, Error> Result<Data, Error>.mapSuccess(
    transform: suspend (value: Data) -> Out
): Result<Out, Error> = when (this) {
    is Result.Success -> Result.Success(transform(this.data))
    is Result.Failure -> Result.Failure(this.error)
}

fun <Data, Error, NE> Result<Data, Error>.mapError(
    transform: (value: Error) -> NE
): Result<Data, NE> = when (this) {
    is Result.Success -> Result.Success(this.data)
    is Result.Failure -> Result.Failure(transform(this.error))
}