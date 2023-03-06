package com.github.jcraane.gptmentorplugin.openapi.response.streaming

import com.github.jcraane.gptmentorplugin.openapi.JSON
import org.junit.Assert.*
import org.junit.Test

class ChatCompletionTest {
    @Test
    fun deserialize() {
        val json = """
            {
              "id": "chatcmpl-6qzKSF4TClOXrawZUZrQ7iSe29R5k",
              "object": "chat.completion.chunk",
              "created": 1678086824,
              "model": "gpt-3.5-turbo-0301",
              "choices": [
                {
                  "delta": {
                    "content": "This"
                  },
                  "index": 0,
                  "finish_reason": null
                }
              ]
            }
        """.trimIndent()

        val chatCompletion = JSON.decodeFromString(ChatCompletion.serializer(), json)

        assertEquals("chatcmpl-6qzKSF4TClOXrawZUZrQ7iSe29R5k", chatCompletion.id)
        assertEquals(1678086824, chatCompletion.created)
        assertEquals("gpt-3.5-turbo-0301", chatCompletion.model)
        assertEquals(1, chatCompletion.choices.size)
        assertEquals("This", chatCompletion.choices[0].delta.content)
    }
}
