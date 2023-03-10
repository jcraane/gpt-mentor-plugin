package com.github.jcraane.gptmentorplugin.actions

import com.github.jcraane.gptmentorplugin.openapi.BasicPrompt

class AddCommentsAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = BasicPrompt.AddComments(code)
}
