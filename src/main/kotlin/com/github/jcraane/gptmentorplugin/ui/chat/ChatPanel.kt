package com.github.jcraane.gptmentorplugin.ui.chat

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import javax.swing.*
import javax.swing.text.StyleConstants
import javax.swing.text.StyledDocument

class ChatPanel : JPanel(), ChatView {
    private val presenter = ChatPresenter(this)

    private val promptTextArea = JBTextArea(
        "Hello, I am GPT-Mentor, your smart coding assistant. Use the build-in prompts or type a " +
                "custom one!"
    ).apply {
        lineWrap = true
        minimumSize = Dimension(Integer.MAX_VALUE, PROMPT_MAX_HEIGHT)
        border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        addKeyListener(object : KeyListener {
            override fun keyTyped(e: java.awt.event.KeyEvent?) {

            }

            override fun keyPressed(e: java.awt.event.KeyEvent?) {
                if (e?.keyCode == KeyEvent.VK_ENTER && e.isMetaDown) {
                    presenter.onSubmitClicked()
                }
            }

            override fun keyReleased(e: java.awt.event.KeyEvent?) {
            }
        })
    }

    private val explanationArea = JTextPane().apply {
        border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
    }

    private val userStyle = explanationArea.addStyle("User", null).apply {
        StyleConstants.setFontFamily(this, "Consolas");
        StyleConstants.setFontSize(this, 14);
        StyleConstants.setForeground(
            this, JBColor(
                Color(77, 111, 151),
                Color(115, 170, 212)
            )
        );
    }

    private val systemStyle = explanationArea.addStyle("System", null).apply {
        StyleConstants.setFontFamily(this, "Consolas");
        StyleConstants.setFontSize(this, 14);
        StyleConstants.setForeground(
            this,
            JBColor(
                Color(103, 81, 111),
                Color(187, 134, 206)
            )
        );
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

    override fun appendPrompt(message: String) {
        ApplicationManager.getApplication().invokeLater {
            val doc = explanationArea.styledDocument
            doc.insertString(explanationArea.styledDocument.length, message, userStyle)
            if (message.endsWith("\n").not() && message.isNotEmpty()) {
                explanationArea.styledDocument.addNewLines(2)
            }
        }
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
            explanationArea.styledDocument.insertString(explanationArea.styledDocument.length, explanation, systemStyle)
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

    override fun onExplanationDone() {
        ApplicationManager.getApplication().invokeLater {
            explanationArea.styledDocument.addNewLines(2)
            promptTextArea.text = ""
        }
        setFocusOnPrompt()
    }

    override fun clearPrompt() {
        ApplicationManager.getApplication().invokeLater {
            promptTextArea.text = ""
        }
    }

    private fun StyledDocument.addNewLines(numberOfLines: Int) {
        val docLength = length
        val newLines = "\n".repeat(numberOfLines)
        insertString(docLength, newLines, null)
    }

    companion object {
        private const val PROMPT_MAX_HEIGHT = 200
    }
}
