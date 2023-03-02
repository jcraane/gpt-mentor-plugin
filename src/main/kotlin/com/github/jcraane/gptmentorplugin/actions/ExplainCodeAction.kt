package com.github.jcraane.gptmentorplugin.actions

import com.github.jcraane.gptmentorplugin.openapi.BasicAction
import com.intellij.openapi.project.Project

class ExplainCodeAction : BaseSimpleChatGptAction() {
    override suspend fun doAction(project: Project, code: String) {
        try {
            val chatGptResponse = openApi.executeBasicAction(
                BasicAction.ExplainCode(code)
            )
            chatGptResponse.choices.firstOrNull()?.message?.content?.let { content ->
                /*SwingUtilities.invokeLater {
                    ShowSuggestionDialog(project, content).show()
                }*/
                showOrUpdateDialog(project, content)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
