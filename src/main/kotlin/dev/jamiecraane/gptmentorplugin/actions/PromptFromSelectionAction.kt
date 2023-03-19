package dev.jamiecraane.gptmentorplugin.actions

class PromptFromSelectionAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = promptFactory.promptFromSelection(code)
}
