package com.github.jcraane.gptmentorplugin.ui

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileTypes.FileTypes
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.ui.EditorTextField
import com.intellij.ui.JBColor
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTextArea
import org.apache.xerces.dom.DocumentImpl
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.JButton
import javax.swing.JComponent
import javax.swing.JPanel

class ShowSuggestionDialog(private val project: Project, private val code: String) : DialogWrapper(false) {
    init {
        title = "Code Snippet"
        isResizable = false
        init()
    }
//    todo also include original question and option to fine tune.

    override fun createCenterPanel(): JComponent {
        val editorFactory = EditorFactory.getInstance()

        val document = editorFactory.createDocument(code)
        val editorTextField = EditorTextField(
            document,
            project,
            FileTypes.PLAIN_TEXT,
        )
        editorTextField.isViewer = true
        editorTextField.border = null
        editorTextField.setOneLineMode(false)
        editorTextField.setCaretPosition(0)
        editorTextField.addSettingsProvider {
            it.settings.isUseSoftWraps=true
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

