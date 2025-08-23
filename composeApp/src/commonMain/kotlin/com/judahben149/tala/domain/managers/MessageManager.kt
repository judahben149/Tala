package com.judahben149.tala.domain.managers

import co.touchlab.kermit.Logger
import com.judahben149.tala.domain.models.common.Result
import com.judahben149.tala.domain.models.conversation.ConversationMessage
import com.judahben149.tala.domain.models.language.Language
import com.judahben149.tala.domain.usecases.gemini.GenerateContentUseCase
import com.judahben149.tala.domain.usecases.messages.GetConversationMessagesUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class MessageManager(
    private val getMessagesUseCase: GetConversationMessagesUseCase,
    private val generateContentUseCase: GenerateContentUseCase,
    private val sessionManager: SessionManager,
    private val logger: Logger
) {

    private val getUserLanguagePreference: Language by lazy {
        sessionManager.getUserLanguagePreference()
    }

    /**
     * Builds the complete prompt for Gemini including system prompt and conversation context
     */
    suspend fun buildGeminiPrompt(conversationId: String, userInput: String? = null): String {
        // Get the last 12 messages to manage context size and latency
        val messages = getMessagesUseCase(conversationId)
            .map { msgList ->
                if (msgList.size <= 12) msgList
                else msgList.takeLast(12)
            }
            .first()

        val language = getUserLanguagePreference
        val basePrompt = buildBasePrompt(language.name)
        
        // Build conversation history
        val conversationHistory = if (messages.isNotEmpty()) {
            messages.joinToString(separator = "\n") { msg ->
                val speaker = if (msg.isUser) "User" else "Assistant"
                "$speaker: ${msg.content.trim()}"
            }
        } else {
            "This is the start of a new conversation."
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
        
        logger.d { "Built Gemini prompt with ${messages.size} messages in $language" }
        return prompt
    }

    /**
     * Generates AI response using Gemini with conversation context
     */
    suspend fun generateResponse(conversationId: String, userInput: String): Result<String, Exception> {
        return try {
            val prompt = buildGeminiPrompt(conversationId, userInput)
            
            when (val response = generateContentUseCase(prompt, emptyList())) {
                is Result.Success -> {
                    if (response.data.candidates.isNotEmpty()) {
                        val text = response.data.candidates[0].content.parts[0].text
                        val processedResponse = processResponse(text)
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
     */
    private fun buildBasePrompt(language: String): String {
        return """
You are Tala, an AI language tutor specializing in $language conversation practice. Your role is to help users improve their $language speaking skills through natural, engaging conversations.

CORE GUIDELINES:
- Always respond in $language only
- Keep responses between 4-50 words for natural conversation flow
- Be encouraging, patient, and supportive
- Maintain a friendly, approachable tone
- Focus on practical, real-world language usage

CONVERSATION STYLE:
- Respond naturally to what the user says
- Ask follow-up questions to keep conversation flowing
- Provide gentle corrections when needed, but don't interrupt the flow
- Introduce new vocabulary contextually
- Use simple, clear $language appropriate to the user's level

CORRECTION APPROACH:
- If user makes an error, gently model the correct form in your response
- Don't explicitly point out every mistake - focus on communication
- Provide positive reinforcement for correct usage
- Help expand vocabulary through context

Remember: Your primary goal is to create a comfortable space for $language practice through natural conversation.
        """.trimIndent()
    }

    /**
     * Processes Gemini's response to ensure it meets word count requirements
     */
    private fun processResponse(response: String): String {
        val cleanedResponse = response.trim()
        val words = cleanedResponse.split(Regex("\\s+")).filter { it.isNotBlank() }
        
        return when {
            words.size < 4 -> {
                // Response too short - return as is but log warning
                logger.w { "Response only ${words.size} words: $cleanedResponse" }
                cleanedResponse
            }
            words.size > 50 -> {
                // Response too long - trim to 50 words and add ellipsis if needed
//                val trimmed = words.take(50).joinToString(" ")
//                logger.w { "Response trimmed from ${words.size} to 50 words" }
//                trimmed
                cleanedResponse
            }
            else -> cleanedResponse
        }
    }

    /**
     * Gets conversation context summary for analytics or debugging
     */
    suspend fun getConversationContext(conversationId: String): ConversationContext {
        val messages = getMessagesUseCase(conversationId).first()
        val language = getUserLanguagePreference.name
        
        return ConversationContext(
            messageCount = messages.size,
            language = language,
            lastMessages = messages.takeLast(20),
            hasContext = messages.isNotEmpty()
        )
    }
}

/**
 * Data class for conversation context information
 */
data class ConversationContext(
    val messageCount: Int,
    val language: String,
    val lastMessages: List<ConversationMessage>,
    val hasContext: Boolean
)
