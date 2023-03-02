package com.github.jcraane.gptmentorplugin.domain

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.junit.Test
import kotlin.test.assertEquals

class ChatGptRequestTest {
    @Test
    fun testDeserialization() {
        // Given
        val json = """{
            "model": "gpt-3.5-turbo",
            "messages": [
                {"role": "user", "content": "Hello!"},
                {"role": "system", "content": "Hi there!"}
            ],
            "temperature": 0.9,
            "maxTokens": 2048
        }"""

        // When
        val request = Json.decodeFromString(ChatGptRequest.serializer(), json)

        // Then
        assertEquals("gpt-3.5-turbo", request.model)
        assertEquals(0.9f, request.temperature)
        assertEquals(2048, request.maxTokens)
        assertEquals(2, request.messages.size)
        assertEquals(ChatGptRequest.Message.Role.USER, request.messages[0].role)
        assertEquals(ChatGptRequest.Message.Role.SYSTEM, request.messages[1].role)
        assertEquals("Hello!", request.messages[0].content)
        assertEquals("Hi there!", request.messages[1].content)
    }
}
