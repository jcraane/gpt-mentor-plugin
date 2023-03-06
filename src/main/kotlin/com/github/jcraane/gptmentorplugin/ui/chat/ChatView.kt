package com.github.jcraane.gptmentorplugin.ui.chat

interface ChatView {
    fun setPrompt(message: String)

    fun clearExplanation()

    fun showError(message: String)

    fun appendExplanation(explanation: String)

    fun showLoading()

    fun getPrompt(): String

    fun clearAll()

    fun setFocusOnPrompt()
}
