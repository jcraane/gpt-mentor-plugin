package dev.jamiecraane.gptmentorplugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class PromptFromFileAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        println("Executed PromptFromFileAction")
    }
}
