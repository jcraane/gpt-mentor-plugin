package com.github.jcraane.gptmentorplugin.actions

import com.github.jcraane.gptmentorplugin.domain.PromptFactory

class PromptFromSelectionAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = PromptFactory.promptFromSelection(code)
}
