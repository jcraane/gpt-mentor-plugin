package dev.jamiecraane.gptmentorplugin.openapi.response.streaming

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatCompletion(
    val id: String,
    val created: Long,
    val model: String,
    val choices: List<Choice>,
)

@Serializable
data class Choice(
    val delta: Delta,
    val index: Int,
    @SerialName("finish_reason")
    val finishReason: String?,
)

@Serializable
data class Delta(
    val content: String? = null,
)
