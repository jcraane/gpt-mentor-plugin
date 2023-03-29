package dev.jamiecraane.gptmentorplugin.actions.editor

import dev.jamiecraane.gptmentorplugin.actions.editor.BaseSimpleChatGptAction

class AddCommentsAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = promptFactory.addComments(code)
}
