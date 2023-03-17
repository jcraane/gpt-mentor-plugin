package com.github.jcraane.gptmentorplugin.ui.history.state

import com.github.jcraane.gptmentorplugin.domain.BasicPrompt
import com.github.jcraane.gptmentorplugin.openapi.request.ChatGptRequest
import com.github.jcraane.gptmentorplugin.ui.chat.ChatContext
import com.github.jcraane.gptmentorplugin.ui.history.state.HistoryItem.Companion.MAX_TITLE_LENGTH
import com.github.jcraane.gptmentorplugin.ui.history.state.HistoryItem.Companion.NO_TITLE_PLACEHOLDER
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HistoryItemTest {
    @Test
    fun createHistoryFromRequest() {
        val prompt = BasicPrompt.Chat(
            messages = listOf(
                ChatGptRequest.Message.newUserMessage("Hello!"),
                ChatGptRequest.Message.newSystemMessage("Hi there!")
            ),
            systemMessage = "System message"
        )

        val from = HistoryItem.from(ChatContext("1", prompt))
        assertTrue(from.id.isNotEmpty())
        // Assert message
        assertEquals("Hello!", from.title)
        assertEquals(2, from.messages.size)
        assertEquals("Hello!", from.messages[0].content)
        assertEquals("Hi there!", from.messages[1].content)
        assertEquals("user", from.messages[0].role)
        assertEquals("system", from.messages[1].role)
    }

    @Test
    fun generateDifferentTitles() {
        val messages = listOf(
            "Hello!",
            "Hi, how are you?",
            "I need help with something",
            "This is a very long message that should be truncated in the title",
            "  This message has leading spaces ",
            "This message has trailing spaces  ",
            "  This message has leading and trailing spaces  ",
            "  This   message    has   multiple  spaces  ",
            "Thismessagehasnospaces",
            "",
            " ",
            "   ",
            "This is a message with a very long word that should be truncated in the title: antidisestablishmentarianism",
            "1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890 1234567890",
        )

        messages.forEachIndexed { index, message ->
            val item = createHistoryItem(message)
            val expectedTitle = when {
                message.isBlank() -> NO_TITLE_PLACEHOLDER
                message.length > MAX_TITLE_LENGTH -> message
                    .replace("\n", "")
                    .substring(0, MAX_TITLE_LENGTH).padEnd(MAX_TITLE_LENGTH + 3, '.')
                else -> message
            }
            assertEquals("Failed at index $index", expectedTitle, item.title)
        }
    }

    private fun createHistoryItem(messageContent: String): HistoryItem {
        return HistoryItem.from(
            ChatContext(
                "1", BasicPrompt.Chat(
                    listOf(
                        ChatGptRequest.Message(ChatGptRequest.Message.Role.USER, messageContent)
                    ), ""
                )
            )
        )
    }
}
