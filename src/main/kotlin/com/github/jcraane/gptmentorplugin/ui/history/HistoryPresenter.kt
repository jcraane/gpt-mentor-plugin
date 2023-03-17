package com.github.jcraane.gptmentorplugin.ui.history

import com.github.jcraane.gptmentorplugin.ui.history.state.HistoryRepository

class HistoryPresenter(
    private val view: HistoryView,
    private val repository: HistoryRepository,
) {
    fun refreshHistory() {
        view.showHistory(repository.getAllHistoryItems())
    }
}

