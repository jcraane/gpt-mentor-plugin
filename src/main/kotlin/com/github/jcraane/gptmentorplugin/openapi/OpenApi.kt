package com.github.jcraane.gptmentorplugin.openapi

import com.github.jcraane.gptmentorplugin.domain.response.ChatGptResponse

interface OpenApi {
    suspend fun executeBasicAction(basicPrompt: BasicPrompt): ChatGptResponse
}

sealed class BasicPrompt(
    open val action: String,
) {
    data class ExplainCode(val code: String) : BasicPrompt("Explain code: $code")
    data class ImproveCode(val code: String) : BasicPrompt("Improve this code: $code")
    data class ReviewCode(val code: String) : BasicPrompt("Review this code: $code")
    data class CreateUnitTest(val code: String) : BasicPrompt("Create a unit test for : $code")
    data class UserDefined(val prompt: String) : BasicPrompt(prompt)
}
