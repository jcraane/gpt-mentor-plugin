package com.github.jcraane.gptmentorplugin.actions

import com.github.jcraane.gptmentorplugin.openapi.BasicAction
import com.github.jcraane.gptmentorplugin.ui.ShowSuggestionDialog
import com.intellij.openapi.project.Project
import javax.swing.SwingUtilities

class ExplainCodeAction : BaseSimpleChatGptAction() {
    override suspend fun doAction(project: Project, code: String) {
        try {
            val chatGptResponse = openApi.executeBasicAction(
                BasicAction.ExplainCode(code))
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
