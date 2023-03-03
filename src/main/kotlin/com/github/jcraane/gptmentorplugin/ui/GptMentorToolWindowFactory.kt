package com.github.jcraane.gptmentorplugin.ui

import com.github.jcraane.gptmentorplugin.messagebus.CHAT_GPT_ACTION_TOPIC
import com.github.jcraane.gptmentorplugin.messagebus.ChatGptApiListener
import com.github.jcraane.gptmentorplugin.openapi.BasicPrompt
import com.github.jcraane.gptmentorplugin.openapi.RealOpenApi
import com.github.jcraane.gptmentorplugin.security.GptMentorCredentialsManager
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import io.ktor.client.*
import kotlinx.coroutines.*
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.*

class GptMentorToolWindowFactory : ToolWindowFactory, ChatGptApiListener {
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var apiJob: Job? = null

    private val openApi = RealOpenApi(
        HttpClient(),
        GptMentorCredentialsManager,
    )

    val promptTextArea = JBTextArea(
        "Hello, I am GPT-Mentor, your smart coding assistant. Use the build-in prompts or type a " +
                "custom one.!"
    ).apply {
        lineWrap = true
    }

    val explanationArea = JBTextArea().apply {
        lineWrap = true
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content: Content = contentFactory.createContent(createAWTComponent(), "", false)
        toolWindow.contentManager.addContent(content)
        subscribeToChatGptActions(project)
    }

    private fun subscribeToChatGptActions(project: Project) {
        project.messageBus.connect().subscribe(CHAT_GPT_ACTION_TOPIC, this)
    }

    fun createAWTComponent(): JComponent {
        val panel = JPanel()
        panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        val layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.layout = layout

        JPanel().apply {
            this.layout = BoxLayout(this, BoxLayout.X_AXIS)
            this.add(JBLabel("Prompt: "))
            this.add(Box.createHorizontalGlue())
        }.also { panel.add(it) }

        panel.add(Box.createVerticalStrut(10))

        panel.add(promptTextArea)

        val submitButton = JButton("Submit")
        submitButton.addActionListener {
            executePrompt(promptTextArea.text)
        }
        val buttonPanel = JPanel().apply {
            this.layout = BoxLayout(this, BoxLayout.X_AXIS)
            add(submitButton)
            panel.add(Box.createHorizontalStrut(5))
            add(JButton("Clear").apply {
                addActionListener {
                    promptTextArea.text = ""
                    explanationArea.text = ""
                }
            })
            this.add(Box.createHorizontalGlue())
        }
        panel.add(Box.createVerticalStrut(10))
        panel.add(buttonPanel)

        JPanel().apply {
            this.layout = BoxLayout(this, BoxLayout.X_AXIS)
            this.add(JBLabel("Explanation: "))
            this.add(Box.createHorizontalGlue())
        }.also { panel.add(it) }
        val explanationLabel = JBLabel("Explanation: ")

        panel.add(Box.createVerticalStrut(10))
        panel.add(explanationArea)

        return panel
    }

    private fun executePrompt(prompt: String) {
        apiJob?.cancel()
        apiJob = scope.launch {
            onLoading()
            onPromptReady(prompt)
            try {
                val chatGptResponse = openApi.executeBasicAction(BasicPrompt.UserDefined(prompt))
                chatGptResponse.choices.firstOrNull()?.message?.content?.let { content ->
                    onExplanationReady(content)
                }
            } catch (e: Exception) {
                onExplanationReady(e.message ?: "Unknown error")
            }
        }
    }

    companion object {
        const val ID = "GPT-Mentor"
    }

    override fun onPromptReady(message: String) {
        ApplicationManager.getApplication().invokeLater {
            promptTextArea.text = message
        }
    }

    override fun onExplanationReady(explanation: String) {
        ApplicationManager.getApplication().invokeLater {
            explanationArea.text = explanation
        }
    }

    override fun onError(message: String) {
        ApplicationManager.getApplication().invokeLater {
            explanationArea.text = message
        }
    }

    override fun onLoading() {
        ApplicationManager.getApplication().invokeLater {
            explanationArea.text = "Loading..."
        }
    }
}
