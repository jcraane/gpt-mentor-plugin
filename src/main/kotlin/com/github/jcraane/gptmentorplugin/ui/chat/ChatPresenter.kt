package com.github.jcraane.gptmentorplugin.ui.chat

import com.github.jcraane.gptmentorplugin.messagebus.CHAT_GPT_ACTION_TOPIC
import com.github.jcraane.gptmentorplugin.messagebus.ChatGptApiListener
import com.github.jcraane.gptmentorplugin.openapi.BasicPrompt
import com.github.jcraane.gptmentorplugin.openapi.OpenApi
import com.github.jcraane.gptmentorplugin.openapi.RealOpenApi
import com.github.jcraane.gptmentorplugin.openapi.StreamingResponse
import com.github.jcraane.gptmentorplugin.security.GptMentorCredentialsManager
import com.intellij.openapi.project.Project
import io.ktor.client.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
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

    fun onAttach(project: Project) {
        project.messageBus.connect().subscribe(CHAT_GPT_ACTION_TOPIC, this)
    }

    fun onSubmitClicked() {
        executeStreaming(chatView.getPrompt())
    }

    private fun executeStreaming(prompt: String) {
        apiJob?.cancel()
        apiJob = scope.launch {
            chatView.setPrompt(prompt)
            kotlin.runCatching {
                openApi.executeBasicActionStreaming(BasicPrompt.UserDefined(prompt))
                    .onStart {
                        chatView.clearExplanation()
                    }
                    .collect { streamingResponse ->
                        when (streamingResponse) {
                            is StreamingResponse.Data -> chatView.appendExplanation(streamingResponse.data)
                            is StreamingResponse.Error -> chatView.showError(streamingResponse.error)
                            StreamingResponse.Done -> {
                                // Do nothing
                            }
                        }
                    }
            }.onFailure {
                if (it !is CancellationException) {
                    chatView.showError(it.message ?: "Unknown error")
                }
            }
        }
    }

    fun onStopClicked() {
        apiJob?.cancel()
    }

    override fun onNewPrompt(prompt: BasicPrompt) {
        executeStreaming(prompt.action)
    }

    fun onNewChatClicked() {
        chatView.clearAll()
        chatView.setFocusOnPrompt()
    }
}
