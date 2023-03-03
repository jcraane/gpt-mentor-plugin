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
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.JComponent
import javax.swing.JPanel

class GptMentorToolWindowFactory : ToolWindowFactory {
    private var editorTextField: EditorTextField? = null

    private val jbLabel = JBLabel("Initial content")

//    todo create a nice layout with a loader and other input fields
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content: Content = contentFactory.createContent(createEditorContent(project, ""), "", false)
        toolWindow.contentManager.addContent(content)

        project.messageBus.connect()
            .subscribe(CHAT_GPT_ACTION_TOPIC, object : ChatGptApiListener {
                override fun onSuccess(message: String) {
                    ApplicationManager.getApplication().invokeLater {
                        jbLabel.text = "OnSuccess"
                        editorTextField?.text = message
                    }
                }

                override fun onError(message: String) {
                    ApplicationManager.getApplication().invokeLater {
                        jbLabel.text = "OnError"
                    }
                }

                override fun onLoading() {
                    ApplicationManager.getApplication().invokeLater {
                        jbLabel.text = "OnLoading"
                    }
                }
            })
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

        val panel = JPanel(BorderLayout())
        panel.add(scrollPane, BorderLayout.CENTER)

        return panel
    }

    companion object {
        const val ID = "GPT-Mentor"
    }
}
