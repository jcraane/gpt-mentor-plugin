package dev.jamiecraane.gptmentorplugin.openapi

import dev.jamiecraane.gptmentorplugin.openapi.request.ChatGptRequest
import kotlinx.coroutines.flow.Flow

interface OpenApi {
    suspend fun executeBasicActionStreaming(chatGptRequest: ChatGptRequest): Flow<StreamingResponse>

    suspend fun stopGenerating()
}

