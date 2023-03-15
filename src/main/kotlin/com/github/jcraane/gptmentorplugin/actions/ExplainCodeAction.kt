package com.github.jcraane.gptmentorplugin.actions

import com.github.jcraane.gptmentorplugin.domain.BasicPrompt

class ExplainCodeAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = BasicPrompt.ExplainCode(code)
}
