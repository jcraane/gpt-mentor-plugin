package dev.jamiecraane.gptmentorplugin.actions

import dev.jamiecraane.gptmentorplugin.domain.BasicPrompt
import dev.jamiecraane.gptmentorplugin.domain.PromptFactory

class AddCommentsAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = promptFactory.addComments(code)
}
