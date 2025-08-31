package com.judahben149.tala.domain.models.haptics

enum class HapticType {
    SELECTION,          // UI element selection/tap
    SUCCESS,            // Successful action completion
    WARNING,            // Warning feedback
    ERROR,              // Error occurred
    LIGHT_IMPACT,       // Subtle feedback
    MEDIUM_IMPACT,      // Standard feedback
    HEAVY_IMPACT,       // Strong feedback
    BUTTON_PRESS,       // Button/click interactions
    TOGGLE_ON,          // Switch/toggle enabled
    TOGGLE_OFF,         // Switch/toggle disabled
    SWIPE,              // Swipe gestures
    SCROLL_TICK,        // List scroll boundaries
    NAVIGATION,         // Screen transitions
    VOICE_START,        // Voice recording start
    VOICE_STOP,         // Voice recording stop
}