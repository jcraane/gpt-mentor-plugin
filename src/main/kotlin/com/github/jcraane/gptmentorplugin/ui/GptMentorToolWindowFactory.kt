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
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import io.ktor.client.*
import kotlinx.coroutines.*
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.swing.*


class GptMentorToolWindowFactory : ToolWindowFactory, ChatGptApiListener {
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

    private val promptTextArea = JBTextArea(
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
        val content: Content = contentFactory.createContent(createMainView(project), "", false)
        toolWindow.contentManager.addContent(content)
        subscribeToChatGptActions(project)
    }

    private fun subscribeToChatGptActions(project: Project) {
        project.messageBus.connect().subscribe(CHAT_GPT_ACTION_TOPIC, this)
    }


    fun createMainView(project: Project): JComponent {
        val promptPanel = createVerticalBoxPanel()
        promptPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        createPromptLabelPanel().also { promptPanel.add(it) }
        promptPanel.add(Box.createVerticalStrut(10))
        val promptScrollPane = createPromptScrollPane()
        promptPanel.add(promptScrollPane)
        promptPanel.add(Box.createVerticalStrut(10))
        val buttonPanel = createButtonPanel(project)
        promptPanel.add(buttonPanel)
        promptPanel.add(Box.createVerticalStrut(10))
        createExplanationLabelPanel().also { promptPanel.add(it) }
        val explanationScrollPane = createExplanationScrollPane()
        promptPanel.add(explanationScrollPane)

        return promptPanel
    }

    private fun createVerticalBoxPanel(): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        return panel
    }

    private fun createPromptLabelPanel(): JPanel {
        val panel = createHorizontalBoxPanel()
        panel.add(JBLabel("Prompt: "))
        panel.add(Box.createHorizontalGlue())
        return panel
    }

    private fun createPromptScrollPane(): JBScrollPane {
        return JBScrollPane(promptTextArea)
    }

    private fun createButtonPanel(project: Project): JPanel {
        val panel = createHorizontalBoxPanel()
        val submitButton = JButton("Submit").apply {
            addActionListener {
                executePrompt(promptTextArea.text)
            }
        }
        panel.add(submitButton)
        panel.add(Box.createHorizontalStrut(5))
        val clearButton = JButton("New Chat")
        clearButton.addActionListener {
            promptTextArea.text = ""
            explanationArea.text = ""
        }
        panel.add(clearButton)

        /*
                val newFileButton = JButton("New File")
                newFileButton.addActionListener {
                    val newFileAction = ActionManager.getInstance().getAction("NewFile")

        //            todo does not work yet.
                    val module = ModuleManager.getInstance(project).modules?.firstOrNull()

                    if (module != null) {
                        ModuleRootManager.getInstance(module).sourceRoots.firstOrNull()?.path?.let { path ->
                            val dataContext = DataManager.getInstance().getDataContext()
                            val event = AnActionEvent.createFromDataContext(path, null, dataContext)
                            newFileAction.actionPerformed(event)
                        }
                    }
                }
                panel.add(newFileButton)
        */

        panel.add(Box.createHorizontalGlue())
        return panel
    }

    private fun createExplanationLabelPanel(): JPanel {
        val panel = createHorizontalBoxPanel()
        panel.add(JBLabel("Explanation: "))
        panel.add(Box.createHorizontalGlue())
        return panel
    }

    private fun createExplanationScrollPane(): JBScrollPane {
        return JBScrollPane(explanationArea)
    }

    private fun createHorizontalBoxPanel(): JPanel {
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.X_AXIS)
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

    override fun onAppendExplanation(explanation: String) {
        ApplicationManager.getApplication().invokeLater {
            val currentText = explanationArea.text
            explanationArea.text = currentText + explanation
        }
    }

    override fun onLoading() {
        ApplicationManager.getApplication().invokeLater {
            explanationArea.text = "Loading..."
        }
    }
}
