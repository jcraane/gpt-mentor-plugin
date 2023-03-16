package com.github.jcraane.gptmentorplugin.ui.history.state

class HistoryRepositoryImpl(
    private val state: HistoryState = HistoryState.getInstance(),
) : HistoryRepository {

    override fun addHistoryItem(item: HistoryItem) {
        val currentHistory = state.history
        state.history = currentHistory.copy(items = currentHistory.items + item)
    }

    override fun deleteHistoryItem(item: HistoryItem) {
        val currentHistory = state.history
        state.history = currentHistory.copy(items = currentHistory.items.filterNot { it == item })
    }

    override fun renameHistoryItem(item: HistoryItem, newName: String) {
        val currentHistory = state.history
        val updatedItems = currentHistory.items.map {
            if (it == item) {
                it.copy(title = newName)
            } else {
                it
            }
        }
        state.history = currentHistory.copy(items = updatedItems)
    }

    override fun getAllHistoryItems(): List<HistoryItem> {
        return state.history.items
    }
}
