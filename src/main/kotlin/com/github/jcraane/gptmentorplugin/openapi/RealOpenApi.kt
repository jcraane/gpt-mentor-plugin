package com.github.jcraane.gptmentorplugin.openapi

import com.github.jcraane.gptmentorplugin.domain.request.ChatGptRequest
import com.github.jcraane.gptmentorplugin.domain.request.chatGptRequest
import com.github.jcraane.gptmentorplugin.domain.response.ChatGptResponse
import com.github.jcraane.gptmentorplugin.openapi.response.streaming.ChatCompletion
import com.github.jcraane.gptmentorplugin.security.GptMentorCredentialsManager
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.sse.EventSources

//todo use streaming API to popuplate explanation in a streaming way (also option to stop generating explanation)
class RealOpenApi(
    private val client: HttpClient,
    private val okHttpClient: OkHttpClient,
    private val credentialsManager: GptMentorCredentialsManager,
) : OpenApi {

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
                setBody(JSON.encodeToString(ChatGptRequest.serializer(), request))
            }.bodyAsText()
        }
    }

    override suspend fun executeBasicAction(basicPrompt: BasicPrompt): ChatGptResponse {
        val request = chatGptRequest {
            message {
                role = ChatGptRequest.Message.Role.USER
                content = basicPrompt.action
            }
        }

        val response = makeHttpRequest(API_ENDPOINT, request)
        return JSON.decodeFromString(ChatGptResponse.serializer(), response)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun executeBasicActionStreaming(basicPrompt: BasicPrompt) =
        callbackFlow {
            val chatGptRequest = basicPrompt.createRequest()

            val request = Request.Builder()
                .url(API_ENDPOINT)
                .header("Authorization", "Bearer ${credentialsManager.getPassword()}")
                .addHeader("Accept", "text/event-stream")
                .addHeader("Content-Type", "application/json")
                .post(JSON.encodeToString(ChatGptRequest.serializer(), chatGptRequest).toRequestBody())
                .build()

            val listener = ChatGptEventSourceListener { response ->
                try {
                    if (response == "[DONE]") {
                        trySend(StreamingResponse.Done)
                    } else {
                        val chatCompletion = JSON.decodeFromString(ChatCompletion.serializer(), response)
                        val content = chatCompletion.choices.firstOrNull()?.delta?.content ?: ""
                        trySend(StreamingResponse.Data(content))
                    }
                } catch (e: Exception) {
                    if (e is CancellationException) {
                        throw e
                    } else {
                        trySend(StreamingResponse.Error(e.message ?: "Unknown error"))
                    }
                }
            }
            val eventSource = EventSources.createFactory(okHttpClient)
                .newEventSource(request = request, listener = listener)

            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    trySend(StreamingResponse.Error(response.message))
                }
            }

            awaitClose {
                eventSource.cancel()
            }
        }

    override suspend fun stopGenerating() {
        TODO("Not yet implemented")
    }

    companion object {
        const val API_ENDPOINT = "https://api.openai.com/v1/chat/completions"
    }
}
