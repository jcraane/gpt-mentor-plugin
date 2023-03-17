package com.github.jcraane.gptmentorplugin.openapi

import com.github.jcraane.gptmentorplugin.openapi.request.ChatGptRequest
import kotlinx.coroutines.flow.Flow

interface OpenApi {
    suspend fun executeBasicActionStreaming(chatGptRequest: ChatGptRequest): Flow<StreamingResponse>

    suspend fun stopGenerating()
}

