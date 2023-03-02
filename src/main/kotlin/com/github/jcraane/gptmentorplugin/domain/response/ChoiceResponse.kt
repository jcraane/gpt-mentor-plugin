package com.github.jcraane.gptmentorplugin.domain.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChoiceResponse(
    @SerialName("finish_reason")
    val finishReason: String,
    @SerialName("index")
    val index: Int,
    @SerialName("message")
    val message: MessageResponse
)
