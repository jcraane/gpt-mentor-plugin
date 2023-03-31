package dev.jamiecraane.gptmentorplugin.domain

import dev.jamiecraane.gptmentorplugin.openapi.JSON
import dev.jamiecraane.gptmentorplugin.openapi.request.ChatGptRequest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Test

class ChatGptRequestTest {
    @Test
    fun testDeserialization() {
        // Given
        val json = """{
            "model": "${Model.GPT_4.code}",
            "messages": [
                {"role": "user", "content": "Hello!"},
                {"role": "system", "content": "Hi there!"}
            ],
            "temperature": 0.9,
            "max_tokens": 2048
        }"""

        // When
        val request = JSON.decodeFromString(ChatGptRequest.serializer(), json)

        // Then
        assertEquals(Model.GPT_4, request.model)
        assertEquals(0.9f, request.temperature)
        assertEquals(2048, request.maxTokens)
        assertEquals(2, request.messages.size)
        assertEquals(ChatGptRequest.Message.Role.USER, request.messages[0].role)
        assertEquals(ChatGptRequest.Message.Role.SYSTEM, request.messages[1].role)
        assertEquals("Hello!", request.messages[0].content)
        assertEquals("Hi there!", request.messages[1].content)
    }

    @Test
    fun testDeserialize() {
        val expectedRequest = ChatGptRequest(
            model = Model.GPT_4,
            temperature = 0.9f,
            maxTokens = 2048,
            messages = listOf(
                ChatGptRequest.Message(ChatGptRequest.Message.Role.USER, "Hello!"),
                ChatGptRequest.Message(ChatGptRequest.Message.Role.SYSTEM, "Hi there!")
            )
        )

        val json = JSON.encodeToString(ChatGptRequest.serializer(), expectedRequest)
        val actualRequest = Json.decodeFromString(ChatGptRequest.serializer(), json)
        assertEquals(expectedRequest, actualRequest)
    }
}
