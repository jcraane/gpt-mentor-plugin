package com.github.jcraane.gptmentorplugin.ui

import com.github.jcraane.gptmentorplugin.messagebus.CHAT_GPT_ACTION_TOPIC
import com.github.jcraane.gptmentorplugin.messagebus.ChatGptApiListener
import com.github.jcraane.gptmentorplugin.openapi.BasicPrompt
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

class ToolWindowPresenter(
    private val view: ToolWindowView,
) : ChatGptApiListener {
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var apiJob: Job? = null

    private val openApi = RealOpenApi(
        client = HttpClient(),
        okHttpClient = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.MINUTES)
            .writeTimeout(10, TimeUnit.MINUTES)
            .build(),
        credentialsManager = GptMentorCredentialsManager,
    )

    fun onAttach(project: Project) {
        project.messageBus.connect().subscribe(CHAT_GPT_ACTION_TOPIC, this)
    }

    fun onSubmitClicked() {
        executeStreaming(view.getPrompt())
    }

    private fun executeStreaming(prompt: String) {
        apiJob?.cancel()
        apiJob = scope.launch {
            onPromptReady(prompt)
            openApi.executeBasicActionStreaming(BasicPrompt.UserDefined(prompt))
                .onStart {
                    onExplanationReady("")
                }
                .collect { streamingResponse ->
                    when (streamingResponse) {
                        is StreamingResponse.Data -> onAppendExplanation(streamingResponse.data)
                        is StreamingResponse.Error -> onError(streamingResponse.error)
                        StreamingResponse.Done -> {
                            // Do nothing
                        }
                    }
                }
        }
    }


    override fun onPromptReady(message: String) {
        view.setPrompt(message)
    }

    override fun onExplanationReady(explanation: String) {
        view.clearExplanation()
    }

    override fun onError(message: String) {
        view.showError(message)
    }

    override fun onAppendExplanation(explanation: String) {
        view.onAppendExplanation(explanation)
    }

    override fun onLoading() {
        view.showLoading()
    }

    fun onNewChatClicked() {
        view.clearAll()
    }
}
