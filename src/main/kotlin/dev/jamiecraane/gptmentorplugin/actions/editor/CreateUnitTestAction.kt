package dev.jamiecraane.gptmentorplugin.actions.editor

import dev.jamiecraane.gptmentorplugin.actions.editor.BaseSimpleChatGptAction

class CreateUnitTestAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = promptFactory.createUnitTest(code)
}
