package com.github.jcraane.gptmentorplugin.domain.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatGptResponse(
    @SerialName("choices")
    val choices: List<ChoiceResponse>,
    @SerialName("created")
    val created: Int,
    @SerialName("id")
    val id: String,
    @SerialName("model")
    val model: String,
    @SerialName("object")
    val objectX: String,
    @SerialName("usage")
    val usage: UsageResponse
)
