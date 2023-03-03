package com.github.jcraane.gptmentorplugin.actions

import com.github.jcraane.gptmentorplugin.openapi.BasicPrompt
import com.intellij.openapi.project.Project

class CreateUnitTestAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = BasicPrompt.CreateUnitTest(code)

    override suspend fun doAction(project: Project, code: String, prompt: BasicPrompt) {
        val chatGptResponse = openApi.executeBasicAction(prompt)
        chatGptResponse.choices.firstOrNull()?.message?.content?.let { content ->
            publishExplanation(project, content)
        }
    }
}
