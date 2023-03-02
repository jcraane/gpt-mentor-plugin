package com.github.jcraane.gptmentorplugin.domain

fun chatGptRequest(block: ChatGptRequestBuilder.() -> Unit): ChatGptRequest {
    return ChatGptRequestBuilder().apply(block).build()
}

class ChatGptRequestBuilder(
    var model: String = "gpt-3.5-turbo",
    var messages: MutableList<MessageBuilder> = mutableListOf(),
    var temperature: Float = 0.8f,
    var maxTokens: Int = 1024
) {
    fun message(block: MessageBuilder.() -> Unit) {
        messages.add(MessageBuilder().apply(block))
    }

    fun build(): ChatGptRequest {
        return ChatGptRequest(
            model = model,
            messages = messages.map { it.build() },
            temperature = temperature,
            maxTokens = maxTokens
        )
    }

    class MessageBuilder(
        var role: ChatGptRequest.Message.Role = ChatGptRequest.Message.Role.USER,
        var content: String = ""
    ) {
        fun role(role: ChatGptRequest.Message.Role) {
            this.role = role
        }

        fun content(content: String) {
            this.content = content
        }

        fun build(): ChatGptRequest.Message {
            return ChatGptRequest.Message(
                role = role,
                content = content
            )
        }
    }
}
