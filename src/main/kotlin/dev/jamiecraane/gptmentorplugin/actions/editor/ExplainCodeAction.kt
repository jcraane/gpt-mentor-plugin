package dev.jamiecraane.gptmentorplugin.actions.editor

import dev.jamiecraane.gptmentorplugin.actions.editor.BaseSimpleChatGptAction

class ExplainCodeAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = promptFactory.explain(code)
}
