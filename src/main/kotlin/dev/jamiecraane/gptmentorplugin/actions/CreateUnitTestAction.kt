package dev.jamiecraane.gptmentorplugin.actions

class CreateUnitTestAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = promptFactory.createUnitTest(code)
}
