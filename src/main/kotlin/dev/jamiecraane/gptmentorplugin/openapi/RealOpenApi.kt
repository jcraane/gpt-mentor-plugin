package dev.jamiecraane.gptmentorplugin.openapi

import dev.jamiecraane.gptmentorplugin.openapi.request.ChatGptRequest
import dev.jamiecraane.gptmentorplugin.openapi.response.ErrorMessage
import dev.jamiecraane.gptmentorplugin.openapi.response.ErrorResponse
import dev.jamiecraane.gptmentorplugin.openapi.response.streaming.ChatCompletion
import dev.jamiecraane.gptmentorplugin.security.GptMentorCredentialsManager
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.util.logging.*
import io.ktor.utils.io.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.sse.EventSource
import okhttp3.sse.EventSources

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

    @OptIn(ExperimentalCoroutinesApi::class)
    override suspend fun executeBasicActionStreaming(chatGptRequest: ChatGptRequest) =
        callbackFlow {
            val request = Request.Builder()
                .url(API_ENDPOINT)
                .header("Authorization", "Bearer ${credentialsManager.getPassword()}")
                .addHeader("Accept", "text/event-stream")
                .addHeader("Content-Type", "application/json")
                .post(JSON.encodeToString(ChatGptRequest.serializer(), chatGptRequest).toRequestBody())
                .build()

            val listener = ChatGptEventSourceListener(
                logger = logger,
                onError = { response ->
                    val message = parseErrorMessage(response?.body?.string())
                        ?: response?.message?.takeIf { it.isNotEmpty() }
                        ?: UNKNOWM_ERROR

                    if (message != "timeout") {
                        trySend(StreamingResponse.Error(message))
                    }
                },
            ) { response ->
                try {
                    if (response == "[DONE]") {
                        trySend(StreamingResponse.Done)
                    } else {
                        val chatCompletion = JSON.decodeFromString(ChatCompletion.serializer(), response)
                        val content = chatCompletion.choices.firstOrNull()?.delta?.content ?: ""
                        trySend(StreamingResponse.Data(content))
                    }
                } catch (e: Exception) {
                    logger.error("Error while processing response", e)
                    if (e is CancellationException) {
                        throw e
                    } else {
                        trySend(StreamingResponse.Error(e.message ?: UNKNOWM_ERROR))
                    }
                }
            }

            val eventSource = EventSources.createFactory(okHttpClient)
                .newEventSource(request = request, listener = listener)

            awaitClose {
                eventSource.cancel()
            }
        }

    private fun parseErrorMessage(json: String?) = json?.let {
        JSON.decodeFromString(ErrorResponse.serializer(), it)
    }?.error?.message

    companion object {
        const val API_ENDPOINT = "https://api.openai.com/v1/chat/completions"
        private const val UNKNOWM_ERROR = "Unknown error"
        private val logger = com.intellij.openapi.diagnostic.Logger.getInstance(RealOpenApi::class.java)
    }
}
