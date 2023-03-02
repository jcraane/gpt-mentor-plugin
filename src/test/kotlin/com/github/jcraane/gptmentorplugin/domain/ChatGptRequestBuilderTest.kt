package com.github.jcraane.gptmentorplugin.domain

import org.junit.Assert.*
import org.junit.Test

class ChatGptRequestBuilderTest {
    @Test
    fun testBuilder() {
        // Given
        val expectedRequest = ChatGptRequest(
            model = "gpt-4.0-turbo",
            temperature = 0.9f,
            maxTokens = 2048,
            messages = listOf(
                ChatGptRequest.Message(ChatGptRequest.Message.Role.USER, "Hello!"),
                ChatGptRequest.Message(ChatGptRequest.Message.Role.SYSTEM, "Hi there!")
            )
        )

        // When
        val actualRequest = ChatGptRequestBuilder().apply {
            model = "gpt-4.0-turbo"
            temperature = 0.9f
            maxTokens = 2048
            message {
                role = ChatGptRequest.Message.Role.USER
                content = "Hello!"
            }
            message {
                role = ChatGptRequest.Message.Role.SYSTEM
                content = "Hi there!"
            }
        }.build()

        // Then
        assertEquals(expectedRequest, actualRequest)
    }
}
