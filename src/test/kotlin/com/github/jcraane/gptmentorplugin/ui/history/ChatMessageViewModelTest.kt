package com.github.jcraane.gptmentorplugin.ui.history

import com.github.jcraane.gptmentorplugin.domain.request.ChatGptRequest
import org.junit.Assert.*
import org.junit.Test

class ChatMessageViewModelTest {
    @Test
    fun `test ChatGptDisplay constructor with messages`() {
        val chatGptRequest = ChatGptRequest(
            model = "gpt-3.5-turbo",
            messages = listOf(
                ChatGptRequest.Message.newUserMessage("Hello"),
                ChatGptRequest.Message.newSystemMessage("Hi there!")
            ),
            temperature = 0.8f,
            maxTokens = 1024,
            stream = false
        )

        val chatGptDisplay = ChatMessageViewModel(chatGptRequest)

        assertEquals("Hello", chatGptDisplay.messages[0].content)
        assertEquals("user", chatGptDisplay.messages[0].role)
        assertEquals("Hi there!", chatGptDisplay.messages[1].content)
        assertEquals("system", chatGptDisplay.messages[1].role)
        assertEquals("Hello", chatGptDisplay.title)
    }

    @Test
    fun `test ChatGptDisplay constructor with blank message content`() {
        val chatGptRequest = ChatGptRequest(
            model = "gpt-3.5-turbo",
            messages = emptyList(),
            temperature = 0.8f,
            maxTokens = 1024,
            stream = false
        )

        val chatGptDisplay = ChatMessageViewModel(chatGptRequest)

        assertTrue(chatGptDisplay.messages.isEmpty())
        assertEquals(ChatMessageViewModel.NO_TITLE_PLACEHOLDER, chatGptDisplay.title)
    }
}
