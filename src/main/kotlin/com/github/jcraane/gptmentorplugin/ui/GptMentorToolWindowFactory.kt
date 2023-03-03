package com.github.jcraane.gptmentorplugin.ui

import com.github.jcraane.gptmentorplugin.messagebus.CHAT_GPT_ACTION_TOPIC
import com.github.jcraane.gptmentorplugin.messagebus.ChatGptApiListener
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.EditorTextField
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import javax.swing.*

class GptMentorToolWindowFactory : ToolWindowFactory {
    private var editorTextField: EditorTextField? = null

    private val jbLabel = JBLabel("Initial content")
    val promptTextArea = JBTextArea(
        "Hello, I am GPT-Mentor, your smart coding assistant. Use the build-in prompts or type a " +
                "custom one.!"
    ).apply {
        lineWrap = true
    }

    val explanationArea = JBTextArea().apply {
        lineWrap = true
    }

    //    todo create a nice layout with a loader and other input fields
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()
//        val content: Content = contentFactory.createContent(createEditorContent(project, ""), "", false)
        val content: Content = contentFactory.createContent(createAWTComponent(), "", false)
        toolWindow.contentManager.addContent(content)

        project.messageBus.connect()
            .subscribe(CHAT_GPT_ACTION_TOPIC, object : ChatGptApiListener {
                override fun onPromptReady(message: String) {
                    promptTextArea.text = message
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
            })
    }

    fun createAWTComponent(): JComponent {
        val panel = JPanel(BorderLayout())

        panel.border = BorderFactory.createEmptyBorder(10, 10, 10, 10)
        val layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.layout = layout
        val promptLabel = JBLabel("Prompt: ")
        panel.add(promptLabel)

        panel.add(Box.createVerticalStrut(10))

        panel.add(promptTextArea)
        val explanationLabel = JBLabel("Explanation: ")

        panel.add(Box.createVerticalStrut(20))
        panel.add(explanationLabel)

        panel.add(Box.createVerticalStrut(10))
        panel.add(explanationArea)

        return panel
    }


    private fun createEditorContent(project: Project, contents: String): JComponent {
        val editorFactory = EditorFactory.getInstance()

        val document = editorFactory.createDocument(contents)
        editorTextField = EditorTextField(
            document,
            project,
            FileTypes.PLAIN_TEXT,
        ).apply {
            isViewer = true
            border = null
            setOneLineMode(false)
            setCaretPosition(0)
            addSettingsProvider {
                it.settings.isUseSoftWraps = true
            }
        }

        val scrollPane = JBScrollPane(editorTextField)

//        private val indicator: ProgressIndicator = ProgressManager.getInstance().progressIndicator
        val panel = JPanel(BorderLayout())
        panel.add(scrollPane, BorderLayout.CENTER)

        return panel
    }

    companion object {
        const val ID = "GPT-Mentor"
    }
}
