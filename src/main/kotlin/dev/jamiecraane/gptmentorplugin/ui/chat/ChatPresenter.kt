package dev.jamiecraane.gptmentorplugin.ui.chat

import com.intellij.openapi.project.Project
import dev.jamiecraane.gptmentorplugin.common.BasicTokenizer
import dev.jamiecraane.gptmentorplugin.common.IdGenerator
import dev.jamiecraane.gptmentorplugin.common.Tokenizer
import dev.jamiecraane.gptmentorplugin.common.UUIDIdGenerator
import dev.jamiecraane.gptmentorplugin.common.extensions.addNewLinesIfNeeded
import dev.jamiecraane.gptmentorplugin.configuration.GptMentorSettingsState
import dev.jamiecraane.gptmentorplugin.domain.BasicPrompt
import dev.jamiecraane.gptmentorplugin.domain.PromptFactory
import dev.jamiecraane.gptmentorplugin.messagebus.CHAT_GPT_ACTION_TOPIC
import dev.jamiecraane.gptmentorplugin.messagebus.ChatGptApiListener
import dev.jamiecraane.gptmentorplugin.openapi.OpenApi
import dev.jamiecraane.gptmentorplugin.openapi.RealOpenApi
import dev.jamiecraane.gptmentorplugin.openapi.StreamingResponse
import dev.jamiecraane.gptmentorplugin.openapi.request.ChatGptRequest
import dev.jamiecraane.gptmentorplugin.security.GptMentorCredentialsManager
import dev.jamiecraane.gptmentorplugin.ui.history.state.HistoryItem
import dev.jamiecraane.gptmentorplugin.ui.history.state.HistoryRepository
import dev.jamiecraane.gptmentorplugin.ui.history.state.PluginStateHistoryRepository
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
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build(),
        credentialsManager = GptMentorCredentialsManager,
    ),
    private val historyRepository: HistoryRepository = PluginStateHistoryRepository(),
    private val idGenerator: IdGenerator = UUIDIdGenerator(),
    private val tokenizer: Tokenizer = BasicTokenizer(),
) : ChatGptApiListener {
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var apiJob: Job? = null

    private val explanationBuilder = StringBuilder()
    private var chatContext: ChatContext = ChatContext(
        chatId = idGenerator.generateId(),
        chat = PromptFactory(GptMentorSettingsState()).chat(emptyList()),
    )
    private var charsTyped = StringBuilder()

    fun onAttach(project: Project) {
        project.messageBus.connect().subscribe(CHAT_GPT_ACTION_TOPIC, this)
    }

    /**
     * Used to update the token count.
     */
    fun promptCharTyped(keyCode: Int) {
        charsTyped.append(keyCode.toChar())
        countTokensAndDisplay(charsTyped.toString())
    }

    /**
     * Handles text deleted from the prompt. Used to update the token count.
     */
    fun promptTextDeleted(length: Int) {
        if (charsTyped.length >= length) {
            charsTyped = charsTyped.delete(charsTyped.length - length, charsTyped.length)
        } else {
            charsTyped = StringBuilder()
        }
        countTokensAndDisplay(charsTyped.toString())
    }

    fun promptPastedFromClipboard(text: String) {
        charsTyped.append(text)
        countTokensAndDisplay(charsTyped.toString())
    }

    private fun countTokensAndDisplay(text: String) {
        val tokens = tokenizer.countTokens(text)
        chatView.updateNumberOfTokens("Approx. $tokens tokens")
    }

    private fun resetTokens() {
        chatView.updateNumberOfTokens("Approx. 0 tokens")
        charsTyped.clear()
    }

    fun onSubmitClicked() {
        val prompt = chatView.getPrompt()

        if (prompt.isEmpty()) {
            return
        }

        chatView.setPrompt(prompt)
        chatContext = chatContext.addMessage(prompt, ChatGptRequest.Message.Role.USER)
        executeStreaming(chatContext.chat)
    }

    private fun executeStreaming(prompt: BasicPrompt) {
        chatView.showLoading()
        charsTyped.clear()
        apiJob?.cancel()
        apiJob = scope.launch {
            chatView.appendPrompt(prompt.action.addNewLinesIfNeeded(1))
            kotlin.runCatching {
                val chatGptRequest = prompt.createRequest()
                openApi.executeBasicActionStreaming(chatGptRequest)
                    .collect { streamingResponse ->
                        handleResponse(streamingResponse)
                    }
            }.onFailure {
                it.printStackTrace()
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

    private fun handleData(data: String) {
        explanationBuilder.append(data)
        chatView.appendExplanation(data)
    }

    private fun handleError(error: String) {
        chatView.showError(error)
        chatView.hideLoading()
        chatView.updateNumberOfTokens("")
    }

    private fun handleDone() {
        chatView.hideLoading()
        chatView.onExplanationDone()
        chatView.clearPrompt()
        chatView.updateNumberOfTokens("")
        chatContext = chatContext.addMessage(explanationBuilder.toString(), ChatGptRequest.Message.Role.SYSTEM)
        historyRepository.addOrUpdateHistoryItem(HistoryItem.from(chatContext))
    }

    fun onStopClicked() {
        chatView.hideLoading()
        apiJob?.cancel()
    }

    override fun onNewPrompt(prompt: BasicPrompt) {
        explanationBuilder.clear()
        chatContext = ChatContext.fromPrompt(id = idGenerator.generateId(), prompt = prompt)

        countTokensAndDisplay(prompt.action)
        chatView.setPrompt(prompt.action, positionCursorAtEnd = prompt.executeImmediate.not())
        chatView.clearExplanation()
        if (prompt.executeImmediate) {
            executeStreaming(prompt)
        }
    }

    override fun loadChatFromHistory(historyItem: HistoryItem) {
        chatContext = historyItem.getChatContext().also { context ->
            resetAll()
            when (context.chat) {
                is BasicPrompt.Chat -> {
                    context.chat.messages.forEach { message ->
                        when (message.role) {
                            ChatGptRequest.Message.Role.USER -> {
                                chatView.appendPrompt(message.content)
                            }

                            ChatGptRequest.Message.Role.SYSTEM -> {
                                chatView.appendExplanation(message.content)
                            }
                        }
                    }
                }

                else -> {
                    chatView.appendPrompt(chatContext.chat.action)
                }
            }
        }
    }

    fun onNewChatClicked() {
        apiJob?.cancel()
        chatContext = ChatContext(
            chatId = idGenerator.generateId(),
            chat = PromptFactory(GptMentorSettingsState()).chat(emptyList()),
        )

        resetAll()
    }

    private fun resetAll() {
        explanationBuilder.clear()
        chatView.clearAll()
        chatView.hideLoading()
        chatView.setFocusOnPrompt()
        resetTokens()
    }
}
