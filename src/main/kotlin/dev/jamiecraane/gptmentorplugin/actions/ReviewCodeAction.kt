package dev.jamiecraane.gptmentorplugin.actions

class ReviewCodeAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = promptFactory.review(code)
}
