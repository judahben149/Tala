package com.judahben149.tala.presentation.screens.home

import androidx.lifecycle.ViewModel
import com.judahben149.tala.domain.managers.SessionManager

class HomeScreenViewModel(
    private val sessionManager: SessionManager
): ViewModel() {

    fun isVoicesSelectionComplete(): Boolean = sessionManager.isVoiceSelectionCompleted()
}