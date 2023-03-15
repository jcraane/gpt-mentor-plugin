package com.github.jcraane.gptmentorplugin.actions

import com.github.jcraane.gptmentorplugin.domain.BasicPrompt
import com.github.jcraane.gptmentorplugin.domain.PromptFactory

class AddCommentsAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = PromptFactory.addComments(code)
}
