package dev.jamiecraane.gptmentorplugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import dev.jamiecraane.gptmentorplugin.messagebus.CHAT_GPT_ACTION_TOPIC

class AppendPromptFromFileAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        val psiFile = e.getData(CommonDataKeys.PSI_FILE)

        if (project == null || psiFile == null) {
            return
        }

        val virtualFile = psiFile.virtualFile
        val contents = String(virtualFile.contentsToByteArray())
        if (contents.isNotEmpty()) {
            project.messageBus.syncPublisher(CHAT_GPT_ACTION_TOPIC).appendToPrompt(contents)
        }
    }
}
