package com.github.jcraane.gptmentorplugin.ui.chat

import com.github.jcraane.gptmentorplugin.openapi.request.ChatGptRequest
import com.github.jcraane.gptmentorplugin.domain.BasicPrompt
import com.github.jcraane.gptmentorplugin.openapi.OpenApi
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

    @Before
    fun setup() {
        presenter = ChatPresenter(chatView, openApi)
    }

    @Test
    fun `onSubmitClicked should set prompt and execute streaming with new user message`() {
            every { chatView.getPrompt() } returns "action"
            presenter.onSubmitClicked()

            coVerify {
                chatView.setPrompt("action")
                chatView.appendPrompt("action")
                openApi.executeBasicActionStreaming(
                    BasicPrompt.Chat(listOf(message), "")
                )
            }
        }

    @Test
    fun `onNewPrompt should set prompt and execute streaming`() {
            presenter.onNewPrompt(prompt)

            coVerify {
                chatView.setPrompt(prompt.action)
                chatView.clearExplanation()
                openApi.executeBasicActionStreaming(prompt)
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

