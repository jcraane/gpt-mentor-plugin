package com.github.jcraane.gptmentorplugin.openapi

import com.github.jcraane.gptmentorplugin.domain.request.ChatGptRequest
import com.github.jcraane.gptmentorplugin.domain.request.chatGptRequest
import com.github.jcraane.gptmentorplugin.domain.response.ChatGptResponse
import kotlinx.coroutines.flow.Flow

interface OpenApi {
    suspend fun executeBasicAction(basicPrompt: BasicPrompt): ChatGptResponse

    suspend fun executeBasicActionStreaming(basicPrompt: BasicPrompt): Flow<StreamingResponse>

    suspend fun stopGenerating()
}

sealed class BasicPrompt(
    open val action: String,
    open val systemPrompt: String,
) {
    open fun createRequest(): ChatGptRequest {
        return chatGptRequest {
            this.systemPrompt(this@BasicPrompt.systemPrompt)
            message {
                role = ChatGptRequest.Message.Role.USER
                content = action
            }

            stream = true
        }
    }

    data class ExplainCode(val code: String) : BasicPrompt(
        "Explain code: \n\n$code", """
        You are an expert AI programmer and an expert in explaining code to medior and junior programmers.

        - Explain the code in concise sentences
        - Provide examples when possible to explain what the code does
    """.trimIndent()
    )

    data class ImproveCode(val code: String) : BasicPrompt("Improve this code: \n\n$code", "You are an expert AI programmer.")
    data class ReviewCode(val code: String) : BasicPrompt(
        "Review this code: \n\n$code", """
        You are an expert AI programmer reviewing code written by others. During the review:

        1. Summarize what is good about the code
        2. Mention where the code can be improved and why if applicable
        5. Check if there are potential security issues
        6. Check if there are potential performance issues
        7. Check if deprecated code is used and suggest alternatives
    """.trimIndent()
    )

    data class CreateUnitTest(val code: String) : BasicPrompt("Create a unit test for : \n\n$code", """
        You are an expert AI programmer which uses unit tests to verify behavior.

        - Write unit tests for the obvious cases
        - Write unit tests for the edge cases
        - DO NOT EXPLAIN THE TEST
    """.trimIndent())
    data class AddComments(val code: String) : BasicPrompt(
        "Add docs to this code: \n\n$code", """
        You are an expert AI programmer which writes code documentation to explain code to other developers.

        - Add documentation for explaining non-obvious things
        - Do not write comments which are obvious in the code
        - ONLY WRITE DOCS
        - DO NOT PROVIDE AN EXPLANATION OF THE CODE
    """.trimIndent()
    )

    data class Chat(val messages: List<ChatGptRequest.Message>) :
        BasicPrompt(messages.lastOrNull()?.content ?: "", "You are a senior AI programmer.") {
        override fun createRequest(): ChatGptRequest {
            return chatGptRequest {
                this@Chat.messages.forEach { message ->
                    message {
                        role = message.role
                        content = message.content
                    }
                }
                stream = true
            }
        }
    }
}
