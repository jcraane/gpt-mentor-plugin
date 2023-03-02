package com.github.jcraane.gptmentorplugin.openapi

import com.github.jcraane.gptmentorplugin.domain.ChatGptRequest
import com.github.jcraane.gptmentorplugin.domain.chatGptRequest
import com.github.jcraane.gptmentorplugin.security.GptMentorCredentialsManager
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

class RealOpenApi(
    private val client: HttpClient,
) : OpenApi {

    private val json = Json {}

    private suspend fun withAuth(block: suspend (apiKey: String) -> String): String {
        return GptMentorCredentialsManager.getPassword()?.let { apiKey ->
            block(apiKey)
        } ?: throw IllegalStateException("No API key found")
    }

    override suspend fun explainCode(
        codeSnippet: String,
    ): String {

        val request = chatGptRequest {
            message {
                role = ChatGptRequest.Message.Role.USER
                content = "Explain the following code: $codeSnippet"
            }
        }

        return withAuth { apiKey ->
            val response = client.post("https://api.openai.com/v1/chat/completions") {
                bearerAuth(apiKey)
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(ChatGptRequest.serializer(), request))
            }

            response.bodyAsText()
        }
    }
}
