package com.github.jcraane.gptmentorplugin.ui.chat

import com.github.jcraane.gptmentorplugin.domain.BasicPrompt
import com.github.jcraane.gptmentorplugin.openapi.request.ChatGptRequest

/**
 * Represents a chat in a messaging system.
 *
 * @param chatId A unique identifier for the chat.
 * @param thread A thread identifier for the chat.
 * @param chat An object representing the chat prompt.
 */
class ChatContext(
    val chatId: String,
    val thread: String,
    val chat: BasicPrompt,
) {
    fun appendThread(data: String) = ChatContext(chatId, this.thread + data, chat)

    fun addMessage(message: String): ChatContext {
        val chatContext = when (chat) {
            is BasicPrompt.Chat -> {
                ChatContext(chatId, thread, chat.copy(messages = chat.messages + ChatGptRequest.Message.newUserMessage(message)))
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
        thread,
        BasicPrompt.Chat(
            messages = listOf(
                ChatGptRequest.Message.newUserMessage(code),
                ChatGptRequest.Message.newUserMessage(newMessage)
            ), systemMessage
        )
    )
}
