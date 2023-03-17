package com.github.jcraane.gptmentorplugin.ui.history.state

import org.junit.Assert.assertEquals
import org.junit.Test


class PluginStateHistoryRepositoryTest {
    private val state: HistoryState = HistoryState()
    private val repository: HistoryRepository = PluginStateHistoryRepository(state)

    @Test
    fun testAddHistoryItem() {
        val currentHistory = History(listOf(HistoryItem("1", "item1", emptyList())))
        state.history = currentHistory

        val newItem = HistoryItem("2", "item2", emptyList())
        repository.addOrUpdateHistoryItem(newItem)

        assertEquals(currentHistory.items + newItem, state.history.items)
    }

    @Test
    fun testDeleteHistoryItem() {
        val itemToDelete = HistoryItem("1", "item1", emptyList())
        val currentHistory = History(listOf(itemToDelete, HistoryItem("1", "item2", emptyList())))
        state.history = currentHistory

        repository.deleteHistoryItem(itemToDelete)
        assertEquals(currentHistory.items.filterNot { it == itemToDelete }, state.history.items)
    }

    @Test
    fun testRenameHistoryItem() {
        val itemToRename = HistoryItem("1", "item1", emptyList())
        val currentHistory = History(listOf(itemToRename, HistoryItem("2", "item2", emptyList())))
        state.history = currentHistory

        val newName = "newName"
        repository.renameHistoryItem(itemToRename, newName)

        val expectedUpdatedItems = currentHistory.items.map {
            if (it == itemToRename) {
                it.copy(title = newName)
            } else {
                it
            }
        }

        assertEquals(currentHistory.copy(items = expectedUpdatedItems), state.history)
    }

    @Test
    fun testGetAllHistoryItems() {
        val currentHistory = History(listOf(HistoryItem("item1", "1", emptyList()), HistoryItem("item2", "2", emptyList())))
        state.history = currentHistory
        val result = repository.getAllHistoryItems()
        assertEquals(currentHistory.items, result)
    }


    @Test
    fun testUpdateExistingHistoryItem() {
        val itemToUpdate = HistoryItem("1", "item1", emptyList())
        val currentHistory = History(listOf(itemToUpdate, HistoryItem("2", "item2", emptyList())))
        state.history = currentHistory

        val updatedItem = itemToUpdate.copy(title = "updatedTitle")
        repository.addOrUpdateHistoryItem(updatedItem)

        val expectedUpdatedItems = currentHistory.items.map {
            if (it == itemToUpdate) {
                updatedItem
            } else {
                it
            }
        }

        assertEquals(currentHistory.copy(items = expectedUpdatedItems), state.history)
    }
}
