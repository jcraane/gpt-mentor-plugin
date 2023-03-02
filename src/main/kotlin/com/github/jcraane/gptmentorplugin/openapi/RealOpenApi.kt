package com.github.jcraane.gptmentorplugin.openapi

import com.github.jcraane.gptmentorplugin.domain.request.ChatGptRequest
import com.github.jcraane.gptmentorplugin.domain.request.chatGptRequest
import com.github.jcraane.gptmentorplugin.domain.response.ChatGptResponse
import com.github.jcraane.gptmentorplugin.security.GptMentorCredentialsManager
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

class RealOpenApi(
    private val client: HttpClient,
    private val credentialsManager: GptMentorCredentialsManager,
) : OpenApi {

    private val json = Json {
        encodeDefaults = true
        coerceInputValues = true
        ignoreUnknownKeys = true
    }

    private suspend fun withAuth(block: suspend (apiKey: String) -> String): String {
        return credentialsManager.getPassword()?.let { apiKey ->
            block(apiKey)
        } ?: throw IllegalStateException("No API key found")
    }

    private suspend fun makeHttpRequest(url: String, request: ChatGptRequest): String {
        return withAuth { apiKey ->
            client.post(url) {
                bearerAuth(apiKey)
                contentType(ContentType.Application.Json)
                setBody(json.encodeToString(ChatGptRequest.serializer(), request))
            }.bodyAsText()
        }
    }

    override suspend fun executeBasicAction(basicAction: BasicAction): ChatGptResponse {
        val request = chatGptRequest {
            message {
                role = ChatGptRequest.Message.Role.USER
                content = basicAction.action
            }
        }

        val response = makeHttpRequest(API_ENDPOINT, request)
        return json.decodeFromString(ChatGptResponse.serializer(), response)
    }

    companion object {
        const val API_ENDPOINT = "https://api.openai.com/v1/chat/completions"
    }
}
