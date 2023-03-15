package com.github.jcraane.gptmentorplugin.ui.chat

import com.github.jcraane.gptmentorplugin.openapi.request.ChatGptRequest
import com.github.jcraane.gptmentorplugin.messagebus.CHAT_GPT_ACTION_TOPIC
import com.github.jcraane.gptmentorplugin.messagebus.ChatGptApiListener
import com.github.jcraane.gptmentorplugin.domain.BasicPrompt
import com.github.jcraane.gptmentorplugin.domain.PromptFactory
import com.github.jcraane.gptmentorplugin.openapi.OpenApi
import com.github.jcraane.gptmentorplugin.openapi.RealOpenApi
import com.github.jcraane.gptmentorplugin.openapi.StreamingResponse
import com.github.jcraane.gptmentorplugin.security.GptMentorCredentialsManager
import com.intellij.openapi.project.Project
import io.ktor.client.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class ChatPresenter(
    private val chatView: ChatView,
    private val openApi: OpenApi = RealOpenApi(
        client = HttpClient(),
        okHttpClient = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .build(),
        credentialsManager = GptMentorCredentialsManager,
    ),
) : ChatGptApiListener {
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var apiJob: Job? = null

    private var chat: BasicPrompt.Chat? = null
    private val explanationBuilder = StringBuilder()

    fun onAttach(project: Project) {
        project.messageBus.connect().subscribe(CHAT_GPT_ACTION_TOPIC, this)
    }

    fun onSubmitClicked() {
        val prompt = chatView.getPrompt()
        chatView.setPrompt(prompt)

        val newChat = (chat ?: PromptFactory.chat(emptyList()))
        val withNewUserMessage = newChat.copy(
            messages = newChat.messages + ChatGptRequest.Message.newUserMessage(prompt)
        )

        chat = withNewUserMessage
        executeStreaming(withNewUserMessage)
    }

    private fun executeStreaming(prompt: BasicPrompt) {
        apiJob?.cancel()
        apiJob = scope.launch {
            chatView.appendPrompt(prompt.action)
            kotlin.runCatching {
                openApi.executeBasicActionStreaming(prompt)
                    .collect { streamingResponse ->
                        handleResponse(streamingResponse)
                    }
            }.onFailure {
                if (it !is CancellationException) {
                    chatView.showError(it.message ?: "Unknown error")
                }
            }
        }
    }

    private fun handleResponse(streamingResponse: StreamingResponse) {
        when (streamingResponse) {
            is StreamingResponse.Data -> handleData(streamingResponse.data)
            is StreamingResponse.Error -> handleError(streamingResponse.error)
            StreamingResponse.Done -> handleDone()
        }
    }

    private fun handleData(data: String): Unit {
        explanationBuilder.append(data)
        chatView.appendExplanation(data)
    }

    private fun handleError(error: String): Unit {
        chatView.showError(error)
    }

    private fun handleDone(): Unit {
        chatView.onExplanationDone()
        chatView.clearPrompt()
        chat = chat?.let {
            it.copy(
                messages = it.messages + ChatGptRequest.Message.newSystemMessage(explanationBuilder.toString())
            )
        }

        explanationBuilder.clear()
    }

    fun onStopClicked() {
        apiJob?.cancel()
    }

    override fun onNewPrompt(prompt: BasicPrompt) {
        chatView.setPrompt(prompt.action)
        chatView.clearExplanation()
        executeStreaming(prompt)
    }

    fun onNewChatClicked() {
        chatView.clearAll()
        chatView.setFocusOnPrompt()
        chat = PromptFactory.chat(emptyList())
    }
}
