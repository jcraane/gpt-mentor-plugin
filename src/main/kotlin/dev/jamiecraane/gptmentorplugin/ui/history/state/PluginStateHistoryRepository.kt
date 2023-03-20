package dev.jamiecraane.gptmentorplugin.ui.history.state

class PluginStateHistoryRepository(
    private val state: HistoryState = HistoryState.getInstance(),
) : HistoryRepository {

    override fun addOrUpdateHistoryItem(item: HistoryItem) {
        val currentHistory = state.history
        val updatedItems = currentHistory.items.map {
            if (it.id == item.id) {
                item
            } else {
                it
            }
        }
        if (!updatedItems.contains(item)) {
            state.history = currentHistory.copy(items = updatedItems + item)
        } else {
            state.history = currentHistory.copy(items = updatedItems)
        }
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
            .sortedByDescending { it.timestamp }
    }
}
