package dev.jamiecraane.gptmentorplugin.domain

import dev.jamiecraane.gptmentorplugin.openapi.request.ChatGptRequest
import dev.jamiecraane.gptmentorplugin.openapi.request.chatGptRequest

sealed class BasicPrompt(
    open val action: String,
    open val systemPrompt: String,
    open val executeImmediate: Boolean,
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

    data class ExplainCode(val code: String, val systemMessage: String) : BasicPrompt(
        action = "Explain code: \n\n$code", systemPrompt = systemMessage.trimIndent(), executeImmediate = true
    )

    data class ImproveCode(val code: String, val systemMessage: String) : BasicPrompt(
        action = "Improve this code: \n\n$code",
        systemPrompt = systemMessage,
        executeImmediate = true
    )

    data class ReviewCode(val code: String, val systemMessage: String) : BasicPrompt(
        action = "Review this code: \n\n$code", systemPrompt = systemMessage, executeImmediate = true
    )

    data class CreateUnitTest(val code: String, val systemMessage: String) : BasicPrompt(
        action = "Create a unit test for : \n\n$code", systemPrompt = systemMessage, executeImmediate = true
    )

    data class AddComments(val code: String, val systemMessage: String) : BasicPrompt(
        action = "$code\n\nAdd javadoc\n", systemPrompt = systemMessage, executeImmediate = true
    )

    data class Chat(val messages: List<ChatGptRequest.Message>, val systemMessage: String) :
        BasicPrompt(action = messages.lastOrNull()?.content ?: "", systemPrompt = systemMessage, executeImmediate = true) {
        override fun createRequest(): ChatGptRequest {
            return chatGptRequest {
                this.systemPrompt(this@Chat.systemPrompt)
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

    data class PromptFromSelection(val code: String, val systemMessage: String) : BasicPrompt(
        action = code, systemPrompt = systemMessage, executeImmediate = false
    )
}
