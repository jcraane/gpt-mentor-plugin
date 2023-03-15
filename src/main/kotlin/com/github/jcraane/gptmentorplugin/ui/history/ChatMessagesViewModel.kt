package com.github.jcraane.gptmentorplugin.ui.history

import com.github.jcraane.gptmentorplugin.openapi.request.ChatGptRequest

data class ChatMessagesViewModel(val messages: List<ChatMessageViewModel>)

data class ChatMessageViewModel(
    val title: String,
    val messages: List<MessageDisplay>,
) {
    data class MessageDisplay(
        val content: String,
        val role: String,
    )

    constructor(chatGptRequest: ChatGptRequest) : this(
        title = chatGptRequest.messages.firstOrNull()?.content?.split(" ")?.take(20)?.joinToString(" ") ?: NO_TITLE_PLACEHOLDER,
        messages = chatGptRequest.messages.map {
            MessageDisplay(
                content = it.content,
                role = it.role.code,
            )
        }
    )

    companion object {
        const val NO_TITLE_PLACEHOLDER = "No title"
    }
}
