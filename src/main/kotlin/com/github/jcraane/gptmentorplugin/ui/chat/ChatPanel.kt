package com.github.jcraane.gptmentorplugin.ui.chat

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

class ChatPanel : JPanel(), ChatView {
    private val presenter = ChatPresenter(this)

    private val promptTextArea = JBTextArea(
        "Hello, I am GPT-Mentor, your smart coding assistant. Use the build-in prompts or type a " +
                "custom one!"
    ).apply {
        lineWrap = true
        minimumSize = Dimension(Integer.MAX_VALUE, PROMPT_MAX_HEIGHT)
        border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
    }

    private val explanationArea = JBTextArea().apply {
        lineWrap = true
        border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
    }

    init {
        layout = BorderLayout()
        add(createMainView(), BorderLayout.CENTER)
    }

    fun onAttach(project: Project) {
        presenter.onAttach(project)
    }

    private fun createMainView(): JComponent {
        val promptPanel = createVerticalBoxPanel()
        promptPanel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)

        createPromptLabelPanel().also { promptPanel.add(it) }
        promptPanel.add(Box.createVerticalStrut(10))
        val promptScrollPane = JBScrollPane(promptTextArea).apply {
            maximumSize = Dimension(Integer.MAX_VALUE, PROMPT_MAX_HEIGHT)
            minimumSize = Dimension(Integer.MAX_VALUE, PROMPT_MAX_HEIGHT)
            preferredSize = Dimension(Integer.MAX_VALUE, PROMPT_MAX_HEIGHT)
        }
        promptPanel.add(promptScrollPane)
        promptPanel.add(Box.createVerticalStrut(10))

        promptPanel.add(Box.createVerticalStrut(10))
        val buttonPanel = createButtonPanel()
        promptPanel.add(buttonPanel)
        promptPanel.add(Box.createVerticalStrut(10))
        createExplanationLabelPanel().also { promptPanel.add(it) }
        promptPanel.add(Box.createVerticalStrut(10))
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

    private fun createButtonPanel(): JPanel {
        val panel = createHorizontalBoxPanel()
        val submitButton = JButton("Submit").apply {
            addActionListener {
                presenter.onSubmitClicked()
            }
        }
        panel.add(submitButton)
        panel.add(Box.createHorizontalStrut(5).apply { maximumSize = Dimension(5, 5) })

        val stopButton = JButton("Stop").apply {
            addActionListener {
                presenter.onStopClicked()
            }
        }
        panel.add(stopButton)

        panel.add(Box.createHorizontalStrut(5).apply { maximumSize = Dimension(5, 5) })
        val newChatButton = JButton("New Chat")
        newChatButton.addActionListener {
            presenter.onNewChatClicked()
        }

        panel.add(newChatButton)
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

    override fun appendExplanation(explanation: String) {
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

    override fun setFocusOnPrompt() {
        promptTextArea.requestFocusInWindow()
    }

    companion object {
        private const val PROMPT_MAX_HEIGHT = 200
    }
}
