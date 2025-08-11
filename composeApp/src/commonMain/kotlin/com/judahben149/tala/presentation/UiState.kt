package com.judahben149.tala.presentation

import com.judahben149.tala.domain.models.common.Result

sealed class UiState<out D, out E> {
    data object Loading : UiState<Nothing, Nothing>()
    data class Loaded<D, E>(val result: Result<D, E>) : UiState<D, E>()
}
