package com.judahben149.tala.domain.managers

import co.touchlab.kermit.Logger
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.conversation.ConversationMessage
import com.judahben149.tala.domain.models.conversation.GuidedPracticeScenario
import com.judahben149.tala.domain.models.conversation.MasteryLevel
import com.judahben149.tala.domain.models.user.Language
import com.judahben149.tala.domain.usecases.conversations.GetMasteryLevelUseCase
import com.judahben149.tala.domain.usecases.gemini.GenerateContentUseCase
import com.judahben149.tala.domain.usecases.messages.GetConversationMessagesUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MessageManager(
    private val getMessagesUseCase: GetConversationMessagesUseCase,
    private val generateContentUseCase: GenerateContentUseCase,
    private val getMasteryLevelUseCase: GetMasteryLevelUseCase,
    private val sessionManager: SessionManager,
    private val logger: Logger,
    private val advancedPromptBuilder: AdvancedPromptBuilder
) {

    private val getUserLanguagePreference: Language by lazy {
        sessionManager.getUserLanguagePreference()
    }

    /**
     * Builds the complete prompt for Gemini including system prompt and conversation context
     * Enhanced to support guided practice scenarios
     */
    suspend fun buildGeminiPrompt(
        conversationId: String,
        userInput: String? = null,
        guidedPracticeScenario: GuidedPracticeScenario? = null,
        topic: String? = null
    ): String {
        logger.d { "Scenario is this -> $guidedPracticeScenario" }
        // Get the last 12 messages to manage context size and latency
        val messages = getMessagesUseCase(conversationId)
            .map { msgList ->
                if (msgList.size <= 12) msgList
                else msgList.takeLast(12)
            }
            .first()

        val language = getUserLanguagePreference
        val masteryLevel = getMasteryLevelUseCase().also { println("Mastery level is ----> $it") }

        // Build appropriate prompt based on mode
        val basePrompt = when {
            guidedPracticeScenario != null -> {
                advancedPromptBuilder.buildGuidedPracticePrompt(
                    language.name,
                    guidedPracticeScenario,
                    masteryLevel
                )
            }
            topic != null -> {
                advancedPromptBuilder.buildTopicSpecificPrompt(
                    language.name,
                    topic,
                    masteryLevel
                )
            }
            else -> buildBasePrompt(language.name, masteryLevel)
        }

        // Build conversation history
        val conversationHistory = if (messages.isNotEmpty()) {
            messages.joinToString(separator = "\n") { msg ->
                val speaker = if (msg.isUser) "User" else "Assistant"
                "$speaker: ${msg.content.trim()}"
            }
        } else {
            val startMessage = when {
                guidedPracticeScenario != null -> "This is the start of a guided practice session: ${guidedPracticeScenario.title}"
                topic != null -> "This is the start of a conversation about: $topic"
                else -> "This is the start of a new conversation."
            }
            startMessage
        }

        // Construct final prompt
        val prompt = buildString {
            append(basePrompt)
            append("\n\nConversation History:\n")
            append(conversationHistory)

            if (userInput != null) {
                append("\nUser: $userInput")
            }

            append("\n\nAssistant:")
        }

        val mode = when {
            guidedPracticeScenario != null -> "guided practice (${guidedPracticeScenario.id})"
            topic != null -> "topic-specific ($topic)"
            else -> "general conversation"
        }

        logger.d { "Built Gemini prompt for $mode with ${messages.size} messages in ${language.name} at ${masteryLevel.name} level" }
        return prompt
    }

    /**
     * Generates AI response using Gemini with conversation context
     * Enhanced to support guided practice scenarios
     */
    suspend fun generateResponse(
        conversationId: String,
        userInput: String,
        guidedPracticeScenario: GuidedPracticeScenario? = null,
        topic: String? = null
    ): Result<String, Exception> {
        return try {
            logger.d { "Scenario is ----> $guidedPracticeScenario" }

            val prompt = buildGeminiPrompt(conversationId, userInput, guidedPracticeScenario, topic)

            when (val response = generateContentUseCase(prompt, emptyList())) {
                is Result.Success -> {
                    if (response.data.candidates.isNotEmpty()) {
                        val text = response.data.candidates[0].content.parts[0].text
                        val masteryLevel = getMasteryLevelUseCase()
                        val processedResponse = processResponse(text, masteryLevel)
                        logger.d { "Generated response: $processedResponse" }
                        Result.Success(processedResponse)
                    } else {
                        logger.e { "No response candidates from Gemini" }
                        Result.Failure(Exception("No response candidates from Gemini"))
                    }
                }
                is Result.Failure -> {
                    logger.e { "Gemini API error: ${response.error}" }
                    Result.Failure(Exception("Failed to generate response: ${response.error.message}"))
                }
            }
        } catch (e: Exception) {
            logger.e(e) { "Error generating response" }
            Result.Failure(e)
        }
    }

    /**
     * Base system prompt that defines Tala's personality and behavior
     * Enhanced with mastery level consideration
     */
    private fun buildBasePrompt(language: String, masteryLevel: MasteryLevel): String {
        val wordLimit = when (masteryLevel) {
            MasteryLevel.BEGINNER -> 15
            MasteryLevel.NOVICE -> 25
            MasteryLevel.INTERMEDIATE -> 35
            MasteryLevel.ADVANCED -> 45
            MasteryLevel.PROFICIENT -> 50
            MasteryLevel.EXPERT -> 60
        }

        val complexityGuidance = when (masteryLevel) {
            MasteryLevel.BEGINNER -> "Use very simple vocabulary and basic grammar. Be extra patient and encouraging."
            MasteryLevel.NOVICE -> "Use common vocabulary with occasional new words. Include simple tenses."
            MasteryLevel.INTERMEDIATE -> "Use varied vocabulary and different tenses. Challenge appropriately."
            MasteryLevel.ADVANCED -> "Use sophisticated vocabulary and complex structures. Encourage fluency."
            MasteryLevel.PROFICIENT -> "Use native-level expressions and cultural references."
            MasteryLevel.EXPERT -> "Use sophisticated, nuanced language for advanced discussions."
        }

        return """
You are Tala, an AI language tutor specializing in $language conversation practice. Your role is to help users improve their $language speaking skills through natural, engaging conversations.

CORE GUIDELINES:
- Always respond in $language only
- Keep responses between 4-$wordLimit words for natural conversation flow
- Be encouraging, patient, and supportive
- Maintain a friendly, approachable tone
- Focus on practical, real-world language usage

MASTERY LEVEL: ${masteryLevel.name}
$complexityGuidance

CONVERSATION STYLE:
- Respond naturally to what the user says
- Ask follow-up questions to keep conversation flowing
- Provide gentle corrections when needed, but don't interrupt the flow
- Introduce new vocabulary contextually
- Use clear $language appropriate to the user's level

CORRECTION APPROACH:
- If user makes an error, gently model the correct form in your response
- Don't explicitly point out every mistake - focus on communication
- Provide positive reinforcement for correct usage
- Help expand vocabulary through context

Remember: Your primary goal is to create a comfortable space for $language practice through natural conversation.
        """.trimIndent()
    }

    /**
     * Processes Gemini's response to ensure it meets word count requirements based on mastery level
     */
    private fun processResponse(response: String, masteryLevel: MasteryLevel): String {
        val cleanedResponse = response.trim()
        val words = cleanedResponse.split(Regex("\\s+")).filter { it.isNotBlank() }
        val maxWords = when (masteryLevel) {
            MasteryLevel.BEGINNER -> 15
            MasteryLevel.NOVICE -> 25
            MasteryLevel.INTERMEDIATE -> 35
            MasteryLevel.ADVANCED -> 45
            MasteryLevel.PROFICIENT -> 50
            MasteryLevel.EXPERT -> 60
        }

        return when {
            words.size < 4 -> {
                // Response too short - return as is but log warning
                logger.w { "Response only ${words.size} words: $cleanedResponse" }
                cleanedResponse
            }
            words.size > maxWords -> {
                // Response too long - trim to max words for mastery level
                val trimmed = words.take(maxWords).joinToString(" ")
                logger.w { "Response trimmed from ${words.size} to $maxWords words for ${masteryLevel.name} level" }
                trimmed
            }
            else -> cleanedResponse
        }
    }

    /**
     * Gets conversation context summary for analytics or debugging
     * Enhanced with guided practice information
     */
    suspend fun getConversationContext(
        conversationId: String,
        guidedPracticeScenario: GuidedPracticeScenario? = null
    ): ConversationContext {
        val messages = getMessagesUseCase(conversationId).first()
        val language = getUserLanguagePreference.name
        val masteryLevel = getMasteryLevelUseCase()

        return ConversationContext(
            messageCount = messages.size,
            language = language,
            masteryLevel = masteryLevel,
            guidedPracticeScenario = guidedPracticeScenario,
            lastMessages = messages.takeLast(20),
            hasContext = messages.isNotEmpty()
        )
    }
}

/**
 * Enhanced data class for conversation context information
 */
data class ConversationContext(
    val messageCount: Int,
    val language: String,
    val masteryLevel: MasteryLevel,
    val guidedPracticeScenario: GuidedPracticeScenario? = null,
    val lastMessages: List<ConversationMessage>,
    val hasContext: Boolean
)