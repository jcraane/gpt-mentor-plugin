package com.github.jcraane.gptmentorplugin.actions

import com.github.jcraane.gptmentorplugin.openapi.BasicAction
import com.github.jcraane.gptmentorplugin.openapi.RealOpenApi
import com.github.jcraane.gptmentorplugin.ui.ShowSuggestionDialog
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.project.Project
import io.ktor.client.*
import kotlinx.coroutines.*
import javax.swing.SwingUtilities

class ImproveCodeAction : BaseSimpleChatGptAction() {
    override suspend fun doAction(project: Project, code: String) {
        try {
            val chatGptResponse = openApi.executeBasicAction(
                BasicAction.ImproveCode(code))
            chatGptResponse.choices.firstOrNull()?.message?.content?.let { content ->
                SwingUtilities.invokeLater {
                    ShowSuggestionDialog(project, content).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
