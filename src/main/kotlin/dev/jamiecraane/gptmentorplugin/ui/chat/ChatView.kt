package dev.jamiecraane.gptmentorplugin.ui.chat

interface ChatView {
    fun appendPrompt(message: String)

    fun setPrompt(message: String, positionCursorAtEnd: Boolean = false)

    fun clearExplanation()

    fun showError(message: String)

    fun appendExplanation(explanation: String)

    fun showLoading()
    fun hideLoading()

    fun getPrompt(): String

    fun clearAll()

    fun setFocusOnPrompt()
    fun onExplanationDone()
    fun clearPrompt()
}
