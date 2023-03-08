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
) {
    open fun createRequest(): ChatGptRequest {
        return chatGptRequest {
            message {
                role = ChatGptRequest.Message.Role.USER
                content = action
            }

            stream = true
        }
    }

    data class ExplainCode(val code: String) : BasicPrompt("Explain code: \n\n$code")
    data class ImproveCode(val code: String) : BasicPrompt("Improve this code: \n\n$code")
    data class ReviewCode(val code: String) : BasicPrompt("Review this code: \n\n$code")
    data class CreateUnitTest(val code: String) : BasicPrompt("Create a unit test for : \n\n$code")
    data class UserDefined(val prompt: String) : BasicPrompt(prompt)
    data class Chat(val messages: List<ChatGptRequest.Message>) : BasicPrompt("") {
        override fun createRequest(): ChatGptRequest {
            return chatGptRequest {
                messages.forEach { message ->
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

