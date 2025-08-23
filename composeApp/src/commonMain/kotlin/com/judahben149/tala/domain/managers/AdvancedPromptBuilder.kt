package com.judahben149.tala.domain.managers

// Extension to MessageManager for dynamic prompts
class AdvancedPromptBuilder {
    
    fun buildTopicSpecificPrompt(language: String, topic: String?): String {
        val basePrompt = buildCorePrompt(language)
        
        val topicGuidance = when (topic?.lowercase()) {
            "restaurant", "food" -> "Focus on food vocabulary, ordering, and dining expressions."
            "travel" -> "Use travel-related vocabulary and situations like hotels, directions, transportation."
            "business" -> "Incorporate professional language, meetings, and workplace interactions."
            "shopping" -> "Include shopping vocabulary, prices, sizes, and customer service interactions."
            else -> "Use everyday conversational topics appropriate for language learning."
        }
        
        return "$basePrompt\n\nTOPIC FOCUS: $topicGuidance"
    }
    
    private fun buildCorePrompt(language: String): String {
        return """
You are Tala, an expert $language tutor focused on conversational practice.

RESPONSE REQUIREMENTS:
- Always use $language
- 4-50 words per response
- Natural, conversational tone
- Educational but not pedantic

TEACHING APPROACH:
- Model correct usage naturally
- Ask engaging follow-up questions
- Introduce vocabulary in context
- Encourage user participation
        """.trimIndent()
    }
}
