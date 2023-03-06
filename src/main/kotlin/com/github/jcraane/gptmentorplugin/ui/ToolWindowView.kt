package com.github.jcraane.gptmentorplugin.ui

import com.intellij.openapi.application.ApplicationManager

interface ToolWindowView {
    fun setPrompt(message: String)

    fun clearExplanation()

    fun showError(message: String)

    fun onAppendExplanation(explanation: String)

    fun showLoading()

    fun getPrompt(): String

    fun clearAll()
}
