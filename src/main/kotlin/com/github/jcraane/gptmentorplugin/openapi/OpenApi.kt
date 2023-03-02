package com.github.jcraane.gptmentorplugin.openapi

interface OpenApi {
    suspend fun explainCode(
        codeSnippet: String): String
}
