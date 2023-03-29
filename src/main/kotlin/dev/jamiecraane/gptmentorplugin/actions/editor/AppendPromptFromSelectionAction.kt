package dev.jamiecraane.gptmentorplugin.actions.editor

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import dev.jamiecraane.gptmentorplugin.messagebus.CHAT_GPT_ACTION_TOPIC

class AppendPromptFromSelectionAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        val editor = e.getData(CommonDataKeys.EDITOR)

        if (project == null || editor == null) {
            return
        }

        val selectedText = editor.selectionModel.selectedText
        if (selectedText?.isNotEmpty() == true) {
            project.messageBus.syncPublisher(CHAT_GPT_ACTION_TOPIC).appendToPrompt(selectedText)
        }
    }
}
