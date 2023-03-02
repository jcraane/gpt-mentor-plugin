package com.github.jcraane.gptmentorplugin.openapi

import com.github.jcraane.gptmentorplugin.domain.response.ChatGptResponse

interface OpenApi {
    suspend fun executeBasicAction(basicAction: BasicAction): ChatGptResponse
}

sealed class BasicAction(
    open val action: String,
) {
    data class ExplainCode(val code: String) : BasicAction("Explain code: $code")
    data class ImproveCode(val code: String) : BasicAction("Improve this code: $code")
    data class ReviewCode(val code: String) : BasicAction("Review this code: $code")
    data class CreateUnitTest(val code: String) : BasicAction("Create a unit test for : $code")
}
