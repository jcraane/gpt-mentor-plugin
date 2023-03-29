package dev.jamiecraane.gptmentorplugin.ui.chat

import dev.jamiecraane.gptmentorplugin.domain.BasicPrompt
import dev.jamiecraane.gptmentorplugin.openapi.request.ChatGptRequest

/**
 * Represents a chat in a messaging system.
 *
 * @param chatId A unique identifier for the chat.
 * @param chat An object representing the chat prompt.
 */
data class ChatContext(
    val chatId: String,
    val chat: BasicPrompt,
) {
    val messages = (chat as? BasicPrompt.Chat)?.messages ?: emptyList()
    fun addMessage(message: String, role: ChatGptRequest.Message.Role): ChatContext {
        val chatContext = when (chat) {
            is BasicPrompt.Chat -> {
                ChatContext(chatId, chat.copy(
                    messages = chat.messages + ChatGptRequest.Message.newMessage(message, role)))
            }

            is BasicPrompt.AddComments -> getUpdatedChat(message, chat.code, chat.systemMessage)
            is BasicPrompt.CreateUnitTest -> getUpdatedChat(message, chat.code, chat.systemMessage)
            is BasicPrompt.ExplainCode -> getUpdatedChat(message, chat.code, chat.systemMessage)
            is BasicPrompt.ImproveCode -> getUpdatedChat(message, chat.code, chat.systemMessage)
            is BasicPrompt.PromptFromSelection,
            -> getUpdatedChat(message, chat.code, chat.systemMessage)

            is BasicPrompt.ReviewCode -> getUpdatedChat(message, chat.code, chat.systemMessage)
        }
        return chatContext
    }

    private fun getUpdatedChat(
        newMessage: String,
        code: String,
        systemMessage: String,
    ) = ChatContext(
        chatId,
        BasicPrompt.Chat(
            messages = listOf(
                ChatGptRequest.Message.newUserMessage(code),
                ChatGptRequest.Message.newUserMessage(newMessage)
            ), systemMessage
        )
    )

    companion object {
        fun fromPrompt(id: String, prompt: BasicPrompt): ChatContext {
            val chat = when (prompt) {
                is BasicPrompt.Chat -> prompt
                else -> BasicPrompt.Chat(
                    messages = listOf(
                        ChatGptRequest.Message.newUserMessage(prompt.action)
                    ),
                    systemMessage = prompt.systemPrompt
                )
            }
            return ChatContext(id, chat)
        }
    }
}
