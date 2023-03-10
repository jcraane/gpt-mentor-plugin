package com.github.jcraane.gptmentorplugin.actions

import com.github.jcraane.gptmentorplugin.messagebus.CHAT_GPT_ACTION_TOPIC
import com.github.jcraane.gptmentorplugin.openapi.BasicPrompt
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys

abstract class BaseSimpleChatGptAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        val editor = e.getData(CommonDataKeys.EDITOR)

        if (project == null || editor == null) {
            return
        }

        val selectedText = editor.selectionModel.selectedText
        selectedText?.let { code ->
            val prompt = createPrompt(code)
            project.messageBus.syncPublisher(CHAT_GPT_ACTION_TOPIC).onNewPrompt(prompt)
        }
    }

    protected abstract fun createPrompt(code: String): BasicPrompt
}
