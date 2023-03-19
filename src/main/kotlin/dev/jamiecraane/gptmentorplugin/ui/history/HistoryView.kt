package dev.jamiecraane.gptmentorplugin.ui.history

import dev.jamiecraane.gptmentorplugin.ui.history.state.HistoryItem

interface HistoryView {
    fun showHistory(history: List<HistoryItem>)
}
