package com.github.jcraane.gptmentorplugin.ui

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.EditorTextField
import com.intellij.ui.components.JBScrollPane
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

//todo this needs to be a toolwindow
class ShowSuggestionDialog(private val project: Project, private val code: String) : DialogWrapper(false) {
    private var editorTextField: EditorTextField? = null

    init {
        title = "Code Snippet"
        isResizable = false
        init()
    }

//    todo also include original question and option to fine tune.

    override fun createCenterPanel(): JComponent {
        val editorFactory = EditorFactory.getInstance()

        val document = editorFactory.createDocument(code)
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
        scrollPane.preferredSize = Dimension(1024, 800)

        val panel = JPanel(BorderLayout())
        panel.add(scrollPane, BorderLayout.CENTER)

        return panel
    }

    override fun createSouthPanel(): JComponent {
        val panel = JPanel(FlowLayout(FlowLayout.RIGHT))
        val closeButton = JButton("Close")
        closeButton.addActionListener {
            dispose()
        }
        panel.add(closeButton)
        return panel
    }
}

