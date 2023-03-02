package com.github.jcraane.gptmentorplugin.domain.response


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MessageResponse(
    @SerialName("content")
    val content: String,
    @SerialName("role")
    val role: String,
)
