package com.github.jcraane.gptmentorplugin.openapi

import com.github.jcraane.gptmentorplugin.domain.response.ChatGptResponse

interface OpenApi {
    suspend fun explainCode(
        codeSnippet: String): ChatGptResponse
}
