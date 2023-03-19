package dev.jamiecraane.gptmentorplugin.ui.chat

import dev.jamiecraane.gptmentorplugin.configuration.GptMentorSettingsState
import dev.jamiecraane.gptmentorplugin.openapi.request.ChatGptRequest
import dev.jamiecraane.gptmentorplugin.domain.BasicPrompt
import dev.jamiecraane.gptmentorplugin.domain.PromptFactory
import dev.jamiecraane.gptmentorplugin.openapi.OpenApi
import dev.jamiecraane.gptmentorplugin.ui.history.state.HistoryRepository
import io.mockk.MockKCancellation
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ChatPresenterTest {

    private lateinit var presenter: ChatPresenter
    private val chatView: ChatView = mockk(relaxed = true)
    private val openApi: OpenApi = mockk(relaxed = true)

    private val prompt = BasicPrompt.ExplainCode("action", "")
    private val message = ChatGptRequest.Message.newUserMessage("action")

    private val historyRepository: HistoryRepository = mockk(relaxed = true)

    @Before
    fun setup() {
        presenter = ChatPresenter(chatView, openApi, historyRepository)
    }

    @Test
    fun `onSubmitClicked should set prompt and execute streaming with new user message`() {
            every { chatView.getPrompt() } returns "action"
            presenter.onSubmitClicked()

            coVerify {
                chatView.setPrompt("action")
                chatView.appendPrompt("action")
                openApi.executeBasicActionStreaming(
                    BasicPrompt.Chat(listOf(message), "").createRequest()
                )
            }
        }

    @Test
    fun `onNewPrompt should set prompt and execute streaming`() {
            presenter.onNewPrompt(prompt)

            coVerify {
                chatView.setPrompt(prompt.action)
                chatView.clearExplanation()
                openApi.executeBasicActionStreaming(prompt.createRequest())
            }
        }

    @Test
    fun `onNewChatClicked should clear all views and reset chat`() {
        presenter.onNewChatClicked()

        coVerify {
            chatView.clearAll()
            chatView.setFocusOnPrompt()
        }
    }
}
