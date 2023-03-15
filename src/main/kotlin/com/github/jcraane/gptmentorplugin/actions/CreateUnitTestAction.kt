package com.github.jcraane.gptmentorplugin.actions

import com.github.jcraane.gptmentorplugin.domain.BasicPrompt

class CreateUnitTestAction : BaseSimpleChatGptAction() {
    override fun createPrompt(code: String) = BasicPrompt.CreateUnitTest(code)
}
