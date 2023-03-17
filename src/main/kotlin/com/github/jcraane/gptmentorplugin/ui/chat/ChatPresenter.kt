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

//todo we need to remember the context (the chat with the id, so we can persist it).
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
    private val promptFactory: PromptFactory = PromptFactory(GptMentorSettingsState.getInstance()),
    private val idGenerator: IdGenerator = UUIDIdGenerator(),
) : ChatGptApiListener {
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var apiJob: Job? = null

    /*private var chat: BasicPrompt.Chat? = null
    private val explanationBuilder = StringBuilder()
    private var chatId: String = idGenerator.generateId()*/

    private var chatContext: ChatContext? = null

    fun onAttach(project: Project) {
        project.messageBus.connect().subscribe(CHAT_GPT_ACTION_TOPIC, this)
    }

    fun onSubmitClicked() {
        val prompt = chatView.getPrompt()
        chatView.setPrompt(prompt)

        chatContext = chatContext?.addMessage(prompt)
        /*val newChat = (chat ?: promptFactory.chat(emptyList()))
        val withNewUserMessage = newChat.copy(
            messages = newChat.messages + ChatGptRequest.Message.newUserMessage(prompt)
        )

        chat = withNewUserMessage*/
        chatContext?.let { executeStreaming(it.chat) }
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
//        explanationBuilder.append(data)
        chatContext = chatContext?.appendThread(data)
        chatView.appendExplanation(data)
    }

    private fun handleError(error: String): Unit {
        chatView.showError(error)
    }

    private fun handleDone(): Unit {
        chatView.onExplanationDone()
        chatView.clearPrompt()
        chatContext?.let { historyRepository.addOrUpdateHistoryItem(HistoryItem.from(it.chatId, it.chat.createRequest())) }
        /*chat = chat?.let {
            val updated = it.copy(
                messages = it.messages + ChatGptRequest.Message.newSystemMessage(explanationBuilder.toString())
            )

            historyRepository.addOrUpdateHistoryItem(HistoryItem.from(chatId, updated.createRequest()))
            updated
        }*/
    }

    fun onStopClicked() {
        apiJob?.cancel()
    }

    override fun onNewPrompt(prompt: BasicPrompt) {
        chatContext = ChatContext(
            chatId = idGenerator.generateId(),
            thread = "",
            chat = PromptFactory(GptMentorSettingsState()).chat(emptyList()),
        )

//        chatId = idGenerator.generateId()
//        explanationBuilder.clear()
//        chat = PromptFactory(GptMentorSettingsState()).chat(emptyList())

        chatView.setPrompt(prompt.action, positionCursorAtEnd = prompt.executeImmediate.not())
        chatView.clearExplanation()
        if (prompt.executeImmediate) {
            executeStreaming(prompt)
        }
    }

    override fun onHistoryItemLoaded(historyItem: HistoryItem) {
//        chatId = historyItem.id
//        todo implement
    }

    fun onNewChatClicked() {
        chatContext = ChatContext(
            chatId = idGenerator.generateId(),
            thread = "",
            chat = PromptFactory(GptMentorSettingsState()).chat(emptyList()),
        )

/*        chatId = idGenerator.generateId()
        explanationBuilder.clear()
        chat = PromptFactory(GptMentorSettingsState()).chat(emptyList())*/

        chatView.clearAll()
        chatView.setFocusOnPrompt()
    }
}
