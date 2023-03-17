package com.github.jcraane.gptmentorplugin.ui.history

import com.github.jcraane.gptmentorplugin.ui.history.state.HistoryItem
import com.github.jcraane.gptmentorplugin.ui.history.state.HistoryRepository

class HistoryPresenter(
    private val view: HistoryView,
    private val repository: HistoryRepository,
) {
    fun refreshHistory() {
        view.showHistory(repository.getAllHistoryItems())
    }

    fun delete(item: HistoryItem) {
        repository.deleteHistoryItem(item)
        refreshHistory()
    }

    fun deleteAll(selectedValues: Array<Any>?) {
        selectedValues?.forEach {
            if (it is HistoryItem) {
                repository.deleteHistoryItem(it)
            }
        }
        refreshHistory()
    }
}

