package com.github.jcraane.gptmentorplugin.actions

class ExplainCodeAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = promptFactory.explain(code)
}
