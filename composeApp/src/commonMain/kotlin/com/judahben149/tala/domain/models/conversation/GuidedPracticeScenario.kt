package com.judahben149.tala.domain.models.conversation

import androidx.compose.ui.graphics.Color

enum class GuidedPracticeScenario(
    val id: String,
    val title: String,
    val description: String,
    val imageResource: String,
    val colorResource: Color,
    val duration: String,
    val difficulty: MasteryLevel = MasteryLevel.BEGINNER
) {
    ORDERING_RESTAURANT(
        id = "restaurant_ordering",
        title = "Ordering at a Restaurant",
        description = "Practice food vocabulary and polite requests",
        imageResource = "ic_restaurant",
        colorResource = Color(0xFF6C63FF), // Purple
        duration = "10-15 min",
        difficulty = MasteryLevel.BEGINNER
    ),
    
    JOB_INTERVIEW(
        id = "job_interview",
        title = "Job Interview Prep", 
        description = "Professional conversation skills",
        imageResource = "ic_briefcase",
        colorResource = Color(0xFF9C27B0), // Deep Purple
        duration = "15-20 min",
        difficulty = MasteryLevel.INTERMEDIATE
    ),
    
    TRAVEL_DIRECTIONS(
        id = "travel_directions",
        title = "Travel & Directions",
        description = "Navigate like a local",
        imageResource = "ic_map",
        colorResource = Color(0xFF7B68EE), // Medium Slate Blue
        duration = "10-15 min",
        difficulty = MasteryLevel.BEGINNER
    ),
    
    SHOPPING_BARGAINING(
        id = "shopping_bargaining",
        title = "Shopping & Bargaining",
        description = "Retail conversation practice",
        imageResource = "ic_shopping",
        colorResource = Color(0xFF8E24AA), // Purple
        duration = "10-15 min",
        difficulty = MasteryLevel.NOVICE
    ),
    
    MEETING_PEOPLE(
        id = "meeting_people",
        title = "Meeting New People",
        description = "Social introductions and small talk",
        imageResource = "ic_handshake",
        colorResource = Color(0xFF9575CD), // Medium Purple
        duration = "10-15 min",
        difficulty = MasteryLevel.BEGINNER
    ),
    
    DOCTOR_VISIT(
        id = "doctor_visit",
        title = "Doctor Visit",
        description = "Health and medical vocabulary",
        imageResource = "ic_medical",
        colorResource = Color(0xFF7E57C2), // Deep Purple
        duration = "15-20 min",
        difficulty = MasteryLevel.INTERMEDIATE
    ),
    
    AIRPORT_HOTEL(
        id = "airport_hotel",
        title = "Airport & Hotel",
        description = "Travel check-in and bookings",
        imageResource = "ic_plane",
        colorResource = Color(0xFF673AB7), // Deep Purple
        duration = "12-18 min",
        difficulty = MasteryLevel.NOVICE
    ),
    
    PHONE_CALLS(
        id = "phone_calls",
        title = "Making Phone Calls",
        description = "Telephone conversations and appointments",
        imageResource = "ic_phone",
        colorResource = Color(0xFF8BC34A), // Light Green
        duration = "8-12 min",
        difficulty = MasteryLevel.INTERMEDIATE
    ),
    
    EMERGENCY_SITUATIONS(
        id = "emergency_situations",
        title = "Emergency Situations",
        description = "Important phrases for urgent situations",
        imageResource = "ic_emergency",
        colorResource = Color(0xFFE91E63), // Pink
        duration = "15-20 min",
        difficulty = MasteryLevel.ADVANCED
    ),
    
    CASUAL_CONVERSATION(
        id = "casual_conversation",
        title = "Casual Daily Chat",
        description = "Everyday topics and friendly conversation",
        imageResource = "ic_chat",
        colorResource = Color(0xFF4CAF50), // Green
        duration = "10-15 min",
        difficulty = MasteryLevel.BEGINNER
    );
    
    companion object {
        fun getByDifficulty(level: MasteryLevel): List<GuidedPracticeScenario> {
            return entries.filter { it.difficulty <= level }
        }
        
        fun getById(id: String): GuidedPracticeScenario? {
            return entries.find { it.id == id }
        }
        
        fun getAllScenarios(): List<GuidedPracticeScenario> {
            return entries
        }
    }
}