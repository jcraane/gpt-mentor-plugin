package dev.jamiecraane.gptmentorplugin.openapi.response

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val error: ErrorMessage? = null,
)

@Serializable
data class ErrorMessage(
    val message: String = "",
    val type: String = "",
    val param: String = "",
    val code: String = ""
)
