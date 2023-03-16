package com.github.jcraane.gptmentorplugin.ui.history.state

import com.github.jcraane.gptmentorplugin.openapi.request.ChatGptRequest
import org.junit.Assert.*
import org.junit.Test

class HistoryItemTest {
    @Test
    fun createHistoryFromRequest() {
        val request = ChatGptRequest(
            model = "gpt-4.0-turbo",
            temperature = 0.9f,
            maxTokens = 2048,
            messages = listOf(
                ChatGptRequest.Message(ChatGptRequest.Message.Role.USER, "Hello!"),
                ChatGptRequest.Message(ChatGptRequest.Message.Role.SYSTEM, "Hi there!")
            )
        )

        val from = HistoryItem.from(request)
        assertTrue(from.id.isNotEmpty())
        // Assert message
        assertEquals(2, from.messages.size)
        assertEquals("Hello!", from.messages[0].content)
        assertEquals("Hi there!", from.messages[1].content)
        assertEquals("user", from.messages[0].role)
        assertEquals("system", from.messages[1].role)
    }
}
