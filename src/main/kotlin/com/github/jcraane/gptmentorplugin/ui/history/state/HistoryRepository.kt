package com.github.jcraane.gptmentorplugin.ui.history.state

interface HistoryRepository {
    fun addOrUpdateHistoryItem(item: HistoryItem)
    fun deleteHistoryItem(item: HistoryItem)
    fun renameHistoryItem(item: HistoryItem, newName: String)
    fun getAllHistoryItems(): List<HistoryItem>
}
