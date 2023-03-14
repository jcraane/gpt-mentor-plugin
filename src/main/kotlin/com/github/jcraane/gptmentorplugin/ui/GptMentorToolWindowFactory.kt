package com.github.jcraane.gptmentorplugin.ui

import com.github.jcraane.gptmentorplugin.ui.chat.ChatPanel
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JOptionPane

class GptMentorToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()

        val tabbedPane = JBTabbedPane().apply {
            addTab("Chat", ChatPanel().apply { onAttach(project) })
            addTab("History", createHistory(listOf("Apple", "Banana", "Orange", "Mango", "Pineapple")))
        }

        val content: Content = contentFactory.createContent(tabbedPane, "", false)
        toolWindow.contentManager.addContent(content)
    }

//    todo double click should load chat with chat history
    private fun createHistory(items: List<String>): JBList<String> {
        return JBList<String>(items).apply {
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    if (e?.clickCount == 2) {
                        val selected = selectedValue
                        JOptionPane.showMessageDialog(this@apply, "You double-clicked on $selected")
                    }
                }
            })
        }
    }

    companion object {
        const val ID = "GPT-Mentor"
    }
}
