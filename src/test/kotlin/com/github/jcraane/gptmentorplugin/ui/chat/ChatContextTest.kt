package com.github.jcraane.gptmentorplugin.ui.chat

import com.github.jcraane.gptmentorplugin.domain.BasicPrompt
import com.github.jcraane.gptmentorplugin.openapi.request.ChatGptRequest
import org.junit.Assert.*
import org.junit.Test

class ChatContextTest {
    @Test
    fun testFromPrompt() {
        // given
        val id = "123"
        val prompt = BasicPrompt.ExplainCode("code", "system message")

        // when
        val chatContext = ChatContext.fromPrompt(id, prompt)

        // then
        assertEquals(id, chatContext.chatId)
        assertTrue(chatContext.chat is BasicPrompt.Chat)
        val chat = chatContext.chat as BasicPrompt.Chat
        assertEquals(1, chat.messages.size)
        assertEquals(ChatGptRequest.Message.newUserMessage(prompt.code), chat.messages[0])
        assertEquals(ChatGptRequest.Message.newUserMessage(prompt.action), chat.messages[1])
        assertEquals(prompt.systemPrompt, chat.systemMessage)
    }
}
