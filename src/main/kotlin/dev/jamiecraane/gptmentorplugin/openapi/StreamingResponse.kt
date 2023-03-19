package dev.jamiecraane.gptmentorplugin.openapi

sealed class StreamingResponse {
    data class Data(val data: String) : StreamingResponse()
    data class Error(val error: String) : StreamingResponse()
    object Done : StreamingResponse()
}
