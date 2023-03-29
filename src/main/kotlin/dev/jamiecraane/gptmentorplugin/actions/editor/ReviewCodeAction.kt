package dev.jamiecraane.gptmentorplugin.actions.editor

import dev.jamiecraane.gptmentorplugin.actions.editor.BaseSimpleChatGptAction

class ReviewCodeAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = promptFactory.review(code)
}
