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

//        todo introduce main mediator or something to handle global actions which trigger actions to panels from other panels
        val tabbedPane = JBTabbedPane().apply {
            addTab("Chat", ChatPanel().apply { onAttach(project) })
            val historyPanel = HistoryPanel()
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
                        println("Unknown tab selected")
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
