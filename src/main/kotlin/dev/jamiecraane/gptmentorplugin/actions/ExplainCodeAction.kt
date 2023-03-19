package dev.jamiecraane.gptmentorplugin.actions

class ExplainCodeAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = promptFactory.explain(code)
}
