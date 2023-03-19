package com.github.jcraane.gptmentorplugin.ui

import com.github.jcraane.gptmentorplugin.ui.chat.ChatPanel
import com.github.jcraane.gptmentorplugin.ui.history.HistoryPanel
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBTabbedPane
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory

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

    companion object {
        const val ID = "GPT-Mentor"
        private const val TAB_CHAT = 0
        private const val TAB_HISTORY = 1
        private const val TAB_HELP = 2
    }
}
