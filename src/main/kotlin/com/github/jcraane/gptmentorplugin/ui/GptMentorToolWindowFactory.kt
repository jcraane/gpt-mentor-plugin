package com.github.jcraane.gptmentorplugin.ui

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import javax.swing.*

class GptMentorToolWindowFactory : ToolWindowFactory, ToolWindowView {
    private val presenter = ToolWindowPresenter(this)

    private val promptTextArea = JBTextArea(
        "Hello, I am GPT-Mentor, your smart coding assistant. Use the build-in prompts or type a " +
                "custom one!"
    ).apply {
        lineWrap = true
    }

    val explanationArea = JBTextArea().apply {
        lineWrap = true
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content: Content = contentFactory.createContent(createMainView(), "", false)
        toolWindow.contentManager.addContent(content)
        presenter.onAttach(project)
    }

    private fun createMainView(): JComponent {
        val promptPanel = createVerticalBoxPanel()
        promptPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        createPromptLabelPanel().also { promptPanel.add(it) }
        promptPanel.add(Box.createVerticalStrut(10))
        val promptScrollPane = createPromptScrollPane()
        promptPanel.add(promptScrollPane)
        promptPanel.add(Box.createVerticalStrut(10))
        val buttonPanel = createButtonPanel()
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

    private fun createButtonPanel(): JPanel {
        val panel = createHorizontalBoxPanel()
        val submitButton = JButton("Submit").apply {
            addActionListener {
                presenter.onSubmitClicked()
            }
        }
        panel.add(submitButton)
        panel.add(Box.createHorizontalStrut(5))
        val clearButton = JButton("New Chat")
        clearButton.addActionListener {
            presenter.onNewChatClicked()
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


    companion object {
        const val ID = "GPT-Mentor"
    }

    override fun setPrompt(message: String) {
        ApplicationManager.getApplication().invokeLater {
            promptTextArea.text = message
        }
    }

    override fun clearExplanation() {
        ApplicationManager.getApplication().invokeLater {
            explanationArea.text = ""
        }
    }

    override fun showError(message: String) {
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

    override fun showLoading() {
        ApplicationManager.getApplication().invokeLater {
            explanationArea.text = "Loading..."
        }
    }

    override fun getPrompt(): String {
        return promptTextArea.text
    }

    override fun clearAll() {
        ApplicationManager.getApplication().invokeLater {
            promptTextArea.text = ""
            explanationArea.text = ""
        }
    }
}
