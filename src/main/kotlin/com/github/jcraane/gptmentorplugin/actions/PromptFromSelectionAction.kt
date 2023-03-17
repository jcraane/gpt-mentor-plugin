package com.github.jcraane.gptmentorplugin.actions

class PromptFromSelectionAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = promptFactory.promptFromSelection(code)
}
