package com.github.jcraane.gptmentorplugin.openapi

import com.github.jcraane.gptmentorplugin.security.GptMentorCredentialsManager
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

class RealOpenApi(
    private val client: HttpClient,
) : OpenApi {

    private suspend fun withAuth(block: suspend (apiKey: String) -> String): String {
        return GptMentorCredentialsManager.getPassword()?.let { apiKey ->
            block(apiKey)
        } ?: throw IllegalStateException("No API key found")
    }
    override suspend fun explainCode(
        codeSnippet: String,
    ): String {
        println("PrintSelectedTextAction.getExplanation")

        return withAuth { apiKey ->
            val response = client.post("https://api.openai.com/v1/chat/completions") {
                bearerAuth(apiKey)
                contentType(ContentType.Application.Json)
//            todo use proper domain object for this
                /*setBody(
                    """
                    {
                        "model": "gpt-3.5-turbo",
                        "messages": [
                            {
                            "role": "user",
                            "content": "Explain the following code: $codeSnippet"
                            }
                        ]
                        "max_tokens": 1024
                    }
                """.trimIndent()*/
                setBody(
                    """
                {
                  "model": "gpt-3.5-turbo",
                  "messages": [{
                    "role": "user",
                    "content": "Explain the following code: fun main() {println(\"Hello World\") }"
                  }],
                  "max_tokens": 1024
                }
            """.trimIndent()
                )
            }

            println("Response: $response")
            response.bodyAsText()
        }
    }
}
