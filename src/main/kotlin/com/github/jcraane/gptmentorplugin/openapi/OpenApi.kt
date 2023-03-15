package com.github.jcraane.gptmentorplugin.openapi

import com.github.jcraane.gptmentorplugin.domain.BasicPrompt
import kotlinx.coroutines.flow.Flow

interface OpenApi {
    suspend fun executeBasicActionStreaming(basicPrompt: BasicPrompt): Flow<StreamingResponse>

    suspend fun stopGenerating()
}

