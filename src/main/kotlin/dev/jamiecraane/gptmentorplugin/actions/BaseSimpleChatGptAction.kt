package dev.jamiecraane.gptmentorplugin.actions

import com.intellij.openapi.actionSystem.ActionManager
import dev.jamiecraane.gptmentorplugin.configuration.GptMentorSettingsState
import dev.jamiecraane.gptmentorplugin.domain.BasicPrompt
import dev.jamiecraane.gptmentorplugin.domain.PromptFactory
import dev.jamiecraane.gptmentorplugin.messagebus.CHAT_GPT_ACTION_TOPIC
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.wm.ToolWindowManager
import dev.jamiecraane.gptmentorplugin.ui.GptMentorToolWindowFactory

abstract class BaseSimpleChatGptAction : AnAction() {
    protected lateinit var promptFactory: PromptFactory

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        val editor = e.getData(CommonDataKeys.EDITOR)

        if (project == null || editor == null) {
            return
        }

        if (!this::promptFactory.isInitialized) {
            promptFactory = PromptFactory(project.getService(GptMentorSettingsState::class.java))
        }

        val selectedText = editor.selectionModel.selectedText
        selectedText?.let { code ->
            val prompt = createPrompt(code)
            project.messageBus.syncPublisher(CHAT_GPT_ACTION_TOPIC).onNewPrompt(prompt)
            ToolWindowManager.getInstance(project).getToolWindow(GptMentorToolWindowFactory.ID)?.show()
        }
    }

    protected abstract fun createPrompt(code: String): BasicPrompt
}
