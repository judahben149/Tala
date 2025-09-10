package com.judahben149.tala.domain.managers

import co.touchlab.kermit.Logger
import com.judahben149.tala.domain.models.conversation.GuidedPracticeScenario
import com.judahben149.tala.domain.models.conversation.MasteryLevel

// Enhanced extension to MessageManager for dynamic prompts
class AdvancedPromptBuilder(
    private val logger: Logger
) {

    /**
     * Builds a guided practice specific prompt with scenario context and mastery level adjustments
     */
    fun buildGuidedPracticePrompt(
        language: String,
        scenario: GuidedPracticeScenario,
        masteryLevel: MasteryLevel
    ): String {
        logger.d { "Building guided practice prompt for scenario: $scenario" }

        val basePrompt = buildCorePrompt(language, masteryLevel)
        val scenarioGuidance = buildScenarioPrompt(scenario, language)
        val masteryGuidance = buildMasteryGuidance(masteryLevel)

        return buildString {
            append(basePrompt)
            append("\n\n")
            append(scenarioGuidance)
            append("\n\n")
            append(masteryGuidance)
            append("\n\nRemember: Stay in character and make this practice session engaging and educational!")
        }
    }

    /**
     * Builds topic-specific prompts for general conversation (your existing method, enhanced)
     */
    fun buildTopicSpecificPrompt(
        language: String,
        topic: String?,
        masteryLevel: MasteryLevel = MasteryLevel.INTERMEDIATE
    ): String {
        val basePrompt = buildCorePrompt(language, masteryLevel)

        val topicGuidance = when (topic?.lowercase()) {
            "restaurant", "food" -> "Focus on food vocabulary, ordering, and dining expressions."
            "travel" -> "Use travel-related vocabulary and situations like hotels, directions, transportation."
            "business" -> "Incorporate professional language, meetings, and workplace interactions."
            "shopping" -> "Include shopping vocabulary, prices, sizes, and customer service interactions."
            else -> "Use everyday conversational topics appropriate for language learning."
        }

        return "$basePrompt\n\nTOPIC FOCUS: $topicGuidance\n\n${buildMasteryGuidance(masteryLevel)}"
    }

    private fun buildCorePrompt(language: String, masteryLevel: MasteryLevel): String {
        val wordLimit = getWordLimitForMastery(masteryLevel)

        return """
You are Tala, an expert $language tutor focused on conversational practice.

RESPONSE REQUIREMENTS:
- Always use $language exclusively
- Keep responses between 4-$wordLimit words
- Natural, conversational tone
- Educational but not pedantic
- Adjust complexity to user's level

TEACHING APPROACH:
- Model correct usage naturally
- Ask engaging follow-up questions
- Introduce vocabulary in context
- Encourage user participation
- Provide gentle corrections when needed
        """.trimIndent()
    }

    private fun buildScenarioPrompt(scenario: GuidedPracticeScenario, language: String): String {
        return when (scenario) {
            GuidedPracticeScenario.ORDERING_RESTAURANT -> """
SCENARIO: You are a friendly waiter/waitress at a cozy $language restaurant.
- Help the customer understand the menu
- Suggest popular dishes
- Take their order politely
- Use vocabulary like
- Be patient with menu questions
            """.trimIndent()

            GuidedPracticeScenario.JOB_INTERVIEW -> """
SCENARIO: You are a hiring manager conducting a job interview in $language.
- Ask about experience and skills
- Discuss the company and role
- Use professional vocabulary
- Be encouraging but professional
- Help practice answering common interview questions
            """.trimIndent()

            GuidedPracticeScenario.TRAVEL_DIRECTIONS -> """
SCENARIO: You are a helpful local person giving directions in $language.
- Help the traveler find their destination
- Use location vocabulary
- Be patient with confused tourists
- Offer alternative routes or transportation
- Include landmarks and clear instructions
            """.trimIndent()

            GuidedPracticeScenario.SHOPPING_BARGAINING -> """
SCENARIO: You are a shop owner in a local market, speaking $language.
- Help customers find what they need
- Discuss prices and accept bargaining
- Use shopping vocabulary
- Be friendly but business-minded
- Explain product features and benefits
            """.trimIndent()

            GuidedPracticeScenario.MEETING_PEOPLE -> """
SCENARIO: You are a friendly local person meeting someone new, speaking $language.
- Make introductions and small talk
- Ask about their background and interests
- Use social vocabulary
- Be warm and welcoming
- Share information about yourself naturally
            """.trimIndent()

            GuidedPracticeScenario.DOCTOR_VISIT -> """
SCENARIO: You are a caring doctor speaking with a patient in $language.
- Ask about symptoms and health concerns
- Explain diagnoses and treatments simply
- Use medical vocabulary
- Be reassuring and professional
- Give clear health advice
            """.trimIndent()

            GuidedPracticeScenario.AIRPORT_HOTEL -> """
SCENARIO: You are a hotel receptionist or airport staff member, speaking $language.
- Help with check-in/check-out procedures
- Assist with travel arrangements
- Use travel vocabulary
- Be professional and helpful
- Handle booking questions and problems
            """.trimIndent()

            GuidedPracticeScenario.PHONE_CALLS -> """
SCENARIO: You are having a phone conversation in $language.
- Practice telephone etiquette and clarity
- Handle appointments and messages
- Use phone vocabulary
- Speak clearly for phone context
- Confirm information and repeat when needed
            """.trimIndent()

            GuidedPracticeScenario.EMERGENCY_SITUATIONS -> """
SCENARIO: You are helping someone in an emergency situation, speaking $language.
- Stay calm and provide clear instructions
- Help access emergency services
- Use emergency vocabulary
- Be direct and reassuring
- Practice important emergency phrases
            """.trimIndent()

            GuidedPracticeScenario.CASUAL_CONVERSATION -> """
SCENARIO: You are a friend having a casual chat in $language.
- Discuss everyday topics naturally
- Share experiences and opinions
- Use casual vocabulary
- Be relaxed and friendly
- Keep conversation flowing naturally
            """.trimIndent()
        }
    }

    private fun buildMasteryGuidance(masteryLevel: MasteryLevel): String {
        return when (masteryLevel) {
            MasteryLevel.BEGINNER -> """
BEGINNER GUIDANCE:
- Use simple, basic vocabulary
- Speak slowly and clearly
- Repeat important words
- Use present tense mostly
- Be very patient and encouraging
            """.trimIndent()

            MasteryLevel.NOVICE -> """
NOVICE GUIDANCE:
- Use common vocabulary with some new words
- Include simple past and future tenses
- Provide gentle corrections
- Ask simple follow-up questions
- Build confidence gradually
            """.trimIndent()

            MasteryLevel.INTERMEDIATE -> """
INTERMEDIATE GUIDANCE:
- Use varied vocabulary and expressions
- Include different tenses naturally
- Challenge with new concepts
- Encourage longer responses
- Provide constructive feedback
            """.trimIndent()

            MasteryLevel.ADVANCED -> """
ADVANCED GUIDANCE:
- Use sophisticated vocabulary
- Include complex grammar structures
- Discuss abstract concepts
- Encourage nuanced expression
- Focus on fluency and accuracy
            """.trimIndent()

            MasteryLevel.PROFICIENT -> """
PROFICIENT GUIDANCE:
- Use native-level expressions
- Include idioms and cultural references
- Discuss complex topics
- Encourage natural conversation flow
- Focus on perfecting subtle nuances
            """.trimIndent()

            MasteryLevel.EXPERT -> """
EXPERT GUIDANCE:
- Use sophisticated, nuanced language
- Include specialized vocabulary
- Engage in complex discussions
- Challenge with advanced concepts
- Focus on maintaining native-level fluency
            """.trimIndent()
        }
    }

    private fun getWordLimitForMastery(masteryLevel: MasteryLevel): Int {
        return when (masteryLevel) {
            MasteryLevel.BEGINNER -> 15
            MasteryLevel.NOVICE -> 25
            MasteryLevel.INTERMEDIATE -> 35
            MasteryLevel.ADVANCED -> 45
            MasteryLevel.PROFICIENT -> 50
            MasteryLevel.EXPERT -> 60
        }
    }
}