package dev.jamiecraane.gptmentorplugin.ui.history

import dev.jamiecraane.gptmentorplugin.ui.history.state.HistoryItem
import dev.jamiecraane.gptmentorplugin.ui.history.state.HistoryRepository

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

    fun rename(item: HistoryItem, newName: String) {
        repository.renameHistoryItem(item, newName)
        refreshHistory()
    }
}

