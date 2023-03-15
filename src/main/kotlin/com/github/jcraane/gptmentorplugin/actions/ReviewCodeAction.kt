package com.github.jcraane.gptmentorplugin.actions

import com.github.jcraane.gptmentorplugin.domain.PromptFactory

class ReviewCodeAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = PromptFactory.review(code)
}
