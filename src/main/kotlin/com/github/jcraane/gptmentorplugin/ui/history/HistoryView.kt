package com.github.jcraane.gptmentorplugin.ui.history

import com.github.jcraane.gptmentorplugin.ui.history.state.HistoryItem

interface HistoryView {
    fun showHistory(history: List<HistoryItem>)
}
