package com.judahben149.tala.domain.usecases.analytics

import com.judahben149.tala.data.local.getCurrentTimeMillis

class TrackConversationTimeUseCase {
    private var sessionStartTime: Long = 0
    
    fun startSession() {
        sessionStartTime = getCurrentTimeMillis()
    }
    
    fun getSessionDuration(): Long {
        return if (sessionStartTime > 0) {
            getCurrentTimeMillis() - sessionStartTime
        } else 0
    }
    
    fun resetSession() {
        sessionStartTime = 0
    }
}