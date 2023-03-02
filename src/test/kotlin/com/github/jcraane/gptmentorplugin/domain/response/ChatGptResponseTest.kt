package com.github.jcraane.gptmentorplugin.domain.response

import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class ChatGptResponseTest {
    @Test
    fun deserialize() {
        val json = """
            {
              "id": "chatcmpl-6plMkRYcELdULGR32n50EE9Nh60uA",
              "object": "chat.completion",
              "created": 1677794822,
              "model": "gpt-3.5-turbo-0301",
              "usage": {
                "prompt_tokens": 28,
                "completion_tokens": 69,
                "total_tokens": 97
              },
              "choices": [
                {
                  "message": {
                    "role": "assistant",
                    "content": "\n\nThis code is checking if the variable \"n\" is equal to zero. If it is, then the function or program that contains this code will return the value of zero. This means that if the input value of \"n\" is zero, the program will not execute any further and will exit or return zero as the output value."
                  },
                  "finish_reason": "stop",
                  "index": 0
                }
              ]
            }
        """.trimIndent()

        val response = Json.decodeFromString(ChatGptResponse.serializer(), json)
        assertEquals("chatcmpl-6plMkRYcELdULGR32n50EE9Nh60uA", response.id)
        assertEquals("chat.completion", response.objectX)
        assertEquals(1, response.choices.size)
        assertEquals("assistant", response.choices[0].message.role)
        assertTrue(response.choices[0].message.content.startsWith("\n\nThis code "))
    }


}
