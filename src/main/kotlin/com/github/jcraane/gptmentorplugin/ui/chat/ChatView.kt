package com.github.jcraane.gptmentorplugin.ui.chat

interface ChatView {
    fun appendPrompt(message: String)

    fun setPrompt(message: String)

    fun clearExplanation()

    fun showError(message: String)

    fun appendExplanation(explanation: String)

    fun showLoading()

    fun getPrompt(): String

    fun clearAll()

    fun setFocusOnPrompt()
    fun onExplanationDone()
    fun clearPrompt()
}
