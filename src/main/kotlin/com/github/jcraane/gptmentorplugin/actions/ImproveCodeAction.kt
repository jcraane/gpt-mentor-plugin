package com.github.jcraane.gptmentorplugin.actions

class ImproveCodeAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = promptFactory.improve(code)
}
