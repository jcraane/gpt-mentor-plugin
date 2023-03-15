package com.github.jcraane.gptmentorplugin.actions

import com.github.jcraane.gptmentorplugin.domain.PromptFactory

class ExplainCodeAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = PromptFactory.explain(code)
}
