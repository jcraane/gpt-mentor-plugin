package dev.jamiecraane.gptmentorplugin.ui.chat

import dev.jamiecraane.gptmentorplugin.domain.BasicPrompt
import dev.jamiecraane.gptmentorplugin.openapi.OpenApi
import dev.jamiecraane.gptmentorplugin.openapi.request.ChatGptRequest
import dev.jamiecraane.gptmentorplugin.ui.history.state.HistoryRepository
import dev.jamiecraane.gptmentorplugin.ui.main.MainPresenter
import io.mockk.Called
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

@Ignore
@ExperimentalCoroutinesApi
class ChatPresenterTest {

    private lateinit var presenter: ChatPresenter
    private val chatView: ChatView = mockk(relaxed = true)
    private val openApi: OpenApi = mockk(relaxed = true)
    private val mainPresenter: MainPresenter = mockk(relaxed = true)

    private val prompt = BasicPrompt.ExplainCode("action", "")
    private val message = ChatGptRequest.Message.newUserMessage("action")

    private val historyRepository: HistoryRepository = mockk(relaxed = true)

    @Before
    fun setup() {
        presenter = ChatPresenter(chatView, mainPresenter, openApi, historyRepository)
    }

    @Test
    fun `onSubmitClicked should set prompt and execute streaming with new user message`() {
        every { chatView.getPrompt() } returns "action"
        presenter.onSubmitClicked()

        coVerify {
            chatView.setPrompt("action")
            chatView.appendToExplanation("action")
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

    @Test
    fun `submit button is clicked with empty prompt`() {
        every { chatView.getPrompt() } returns ""

        presenter.onSubmitClicked()

        coVerify {
            openApi wasNot Called
        }
    }
}

