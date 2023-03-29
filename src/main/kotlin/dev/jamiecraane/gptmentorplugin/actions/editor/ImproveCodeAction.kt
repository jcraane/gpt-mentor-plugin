package dev.jamiecraane.gptmentorplugin.actions.editor

import dev.jamiecraane.gptmentorplugin.actions.editor.BaseSimpleChatGptAction

class ImproveCodeAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = promptFactory.improve(code)
}
