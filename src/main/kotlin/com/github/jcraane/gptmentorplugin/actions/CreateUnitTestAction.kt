package com.github.jcraane.gptmentorplugin.actions

import com.github.jcraane.gptmentorplugin.openapi.BasicAction
import com.intellij.openapi.project.Project

class CreateUnitTestAction : BaseSimpleChatGptAction() {
    override suspend fun doAction(project: Project, code: String) {
        try {
            val chatGptResponse = openApi.executeBasicAction(
                BasicAction.CreateUnitTest(code)
            )
            chatGptResponse.choices.firstOrNull()?.message?.content?.let { content ->
                publishResult(project, content)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}
