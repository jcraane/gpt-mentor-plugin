package com.github.jcraane.gptmentorplugin.ui.chat

import com.github.jcraane.gptmentorplugin.common.IdGenerator
import com.github.jcraane.gptmentorplugin.common.UUIDIdGenerator
import com.github.jcraane.gptmentorplugin.configuration.GptMentorSettingsState
import com.github.jcraane.gptmentorplugin.domain.BasicPrompt
import com.github.jcraane.gptmentorplugin.domain.PromptFactory
import com.github.jcraane.gptmentorplugin.messagebus.CHAT_GPT_ACTION_TOPIC
import com.github.jcraane.gptmentorplugin.messagebus.ChatGptApiListener
import com.github.jcraane.gptmentorplugin.openapi.OpenApi
import com.github.jcraane.gptmentorplugin.openapi.RealOpenApi
import com.github.jcraane.gptmentorplugin.openapi.StreamingResponse
import com.github.jcraane.gptmentorplugin.openapi.request.ChatGptRequest
import com.github.jcraane.gptmentorplugin.security.GptMentorCredentialsManager
import com.github.jcraane.gptmentorplugin.ui.history.state.HistoryItem
import com.github.jcraane.gptmentorplugin.ui.history.state.HistoryRepository
import com.github.jcraane.gptmentorplugin.ui.history.state.PluginStateHistoryRepository
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
    private val historyRepository: HistoryRepository = PluginStateHistoryRepository(),
    private val idGenerator: IdGenerator = UUIDIdGenerator(),
) : ChatGptApiListener {
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var apiJob: Job? = null

    private val explanationBuilder = StringBuilder()
    private var chatContext: ChatContext = ChatContext(
        chatId = idGenerator.generateId(),
        chat = PromptFactory(GptMentorSettingsState()).chat(emptyList()),
    )

    fun onAttach(project: Project) {
        project.messageBus.connect().subscribe(CHAT_GPT_ACTION_TOPIC, this)
    }

    fun onSubmitClicked() {
        val prompt = chatView.getPrompt()
        chatView.setPrompt(prompt)

        chatContext = chatContext.addMessage(prompt, ChatGptRequest.Message.Role.USER)
        executeStreaming(chatContext.chat)
    }

    private fun executeStreaming(prompt: BasicPrompt) {
        apiJob?.cancel()
        apiJob = scope.launch {
            chatView.appendPrompt(prompt.action)
            kotlin.runCatching {
                val chatGptRequest = prompt.createRequest()
                openApi.executeBasicActionStreaming(chatGptRequest)
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
        chatContext = chatContext.addMessage(explanationBuilder.toString(), ChatGptRequest.Message.Role.SYSTEM)
        historyRepository.addOrUpdateHistoryItem(HistoryItem.from(chatContext))
    }

    fun onStopClicked() {
        apiJob?.cancel()
    }

    override fun onNewPrompt(prompt: BasicPrompt) {
        explanationBuilder.clear()
        chatContext = ChatContext.fromPrompt(id = idGenerator.generateId(), prompt = prompt)

        chatView.setPrompt(prompt.action, positionCursorAtEnd = prompt.executeImmediate.not())
        chatView.clearExplanation()
        if (prompt.executeImmediate) {
            executeStreaming(prompt)
        }
    }

    override fun loadChatFromHistory(historyItem: HistoryItem) {
        chatContext = historyItem.getChatContext().also { context ->
            explanationBuilder.clear()
            chatView.clearAll()
            chatView.setFocusOnPrompt()
            val explanation = when (context.chat) {
                is BasicPrompt.Chat -> {
                    context.chat.messages
                        .joinToString("\n") { it.content }
                }

                else -> {
                    chatContext.chat.action
                }
            }
            chatView.appendExplanation(explanation)
        }
    }

    fun onNewChatClicked() {
        explanationBuilder.clear()
        chatContext = ChatContext(
            chatId = idGenerator.generateId(),
            chat = PromptFactory(GptMentorSettingsState()).chat(emptyList()),
        )

        chatView.clearAll()
        chatView.setFocusOnPrompt()
    }
}
