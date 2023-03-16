package com.github.jcraane.gptmentorplugin.ui.history.state

interface HistoryRepository {
    fun addHistoryItem(item: HistoryItem)
    fun deleteHistoryItem(item: HistoryItem)
    fun renameHistoryItem(item: HistoryItem, newName: String)
    fun getAllHistoryItems(): List<HistoryItem>
}
