package com.github.jcraane.gptmentorplugin.domain

import com.github.jcraane.gptmentorplugin.openapi.request.ChatGptRequest
import com.github.jcraane.gptmentorplugin.openapi.request.chatGptRequest

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

    data class ExplainCode(val code: String, val systemMessage: String) : BasicPrompt(
        "Explain code: \n\n$code", systemMessage.trimIndent()
    )

    data class ImproveCode(val code: String, val systemMessage: String) : BasicPrompt("Improve this code: \n\n$code", systemMessage)
    data class ReviewCode(val code: String, val systemMessage: String) : BasicPrompt(
        "Review this code: \n\n$code", systemMessage
    )

    data class CreateUnitTest(val code: String, val systemMessage: String) : BasicPrompt(
        "Create a unit test for : \n\n$code", systemMessage
    )

    data class AddComments(val code: String, val systemMessage: String) : BasicPrompt(
        "Add docs to this code: \n\n$code", systemMessage
    )

    data class Chat(val messages: List<ChatGptRequest.Message>, val systemMessage: String) :
        BasicPrompt(messages.lastOrNull()?.content ?: "", systemMessage) {
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
