package com.github.jcraane.gptmentorplugin.ui

import com.github.jcraane.gptmentorplugin.ui.chat.ChatPanel
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.Content
import com.intellij.ui.content.ContentFactory

class GptMentorToolWindowFactory : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val contentFactory = ContentFactory.SERVICE.getInstance()

        val chatView = ChatPanel().apply {
            onAttach(project)
        }

        val content: Content = contentFactory.createContent(chatView, "", false)
        toolWindow.contentManager.addContent(content)
    }

    companion object {
        const val ID = "GPT-Mentor"
    }
}
