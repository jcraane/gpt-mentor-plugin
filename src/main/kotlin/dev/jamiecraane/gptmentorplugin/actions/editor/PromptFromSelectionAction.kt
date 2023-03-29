package dev.jamiecraane.gptmentorplugin.actions.editor

import dev.jamiecraane.gptmentorplugin.actions.editor.BaseSimpleChatGptAction

class PromptFromSelectionAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = promptFactory.promptFromSelection(code)
}
