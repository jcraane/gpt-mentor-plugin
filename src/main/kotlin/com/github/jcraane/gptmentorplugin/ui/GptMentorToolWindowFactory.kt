package com.github.jcraane.gptmentorplugin.ui

import com.github.jcraane.gptmentorplugin.ui.chat.ChatPanel
import com.github.jcraane.gptmentorplugin.ui.history.HistoryPanel
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.components.JBTextArea
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.JPanel

class GptMentorToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()

        val tabbedPane = JBTabbedPane().apply {
            val chatPanel = ChatPanel().apply { onAttach(project) }
            addTab("Chat", chatPanel)
            val historyPanel = HistoryPanel { historyItem ->
                chatPanel.presenter.loadChatFromHistory(historyItem)
                this.selectedIndex = TAB_CHAT
            }
            addTab("History", historyPanel)
            addTab("Help", createHelpPanel())
            addChangeListener {
                when (selectedIndex) {
                    TAB_CHAT -> {
                        println("Chat tab selected")
                    }

                    TAB_HISTORY -> {
                        historyPanel.presenter.refreshHistory()
                    }

                    TAB_HELP -> {
                        println("Help tab selected")
                    }

                    else -> {
                        // Do nothing
                    }
                }
            }
        }

        val content: Content = contentFactory.createContent(tabbedPane, "", false)
        toolWindow.contentManager.addContent(content)
    }

    private fun createHelpPanel(): Component {
        return JPanel().apply {
            layout = BorderLayout()
            val jbTextArea = JBTextArea(
                "GPT-Mentor which is powered by Open AI is a plugin that helps you to improve your code. It can explain your " +
                        "code, improve your code, review your code, create unit tests and add comments to your code.\n\nIt also enables you to create custom chats. \n\nThe default shortcuts for the standard actions are:\n- Explain Code: Ctrl + Alt + Shift + E\n- Improve Code: Ctrl + Alt + Shift + I\n- Review Code: Ctrl + Alt + Shift + R\n- Create Unit Test: Ctrl + Alt + Shift + T\n- Add Comments: Ctrl + Alt + Shift + C\n\nThe history view displays the chat history. You can also remove messages from the history. Double click on a chat in the history view to open the chat in the editor."
            ).apply {
                lineWrap = true
                isEditable = false
            }

            val scrollPane = JBScrollPane(jbTextArea)
            add(scrollPane, BorderLayout.CENTER)
        }
    }

    companion object {
        const val ID = "GPT-Mentor"
        private const val TAB_CHAT = 0
        private const val TAB_HISTORY = 1
        private const val TAB_HELP = 2
    }
}
