package com.github.jcraane.gptmentorplugin.domain.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UsageResponse(
    @SerialName("completion_tokens")
    val completionTokens: Int,
    @SerialName("prompt_tokens")
    val promptTokens: Int,
    @SerialName("total_tokens")
    val totalTokens: Int
)
