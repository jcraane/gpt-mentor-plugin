package com.github.jcraane.gptmentorplugin.ui.history.state

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Test


class HistoryRepositoryImplTest {
    private val state: HistoryState = HistoryState()
    private val repository: HistoryRepository = HistoryRepositoryImpl(state)

    @Test
    fun testAddHistoryItem() {
        val currentHistory = History(listOf(HistoryItem("item1", emptyList())))
        state.history = currentHistory

        val newItem = HistoryItem("item2", emptyList())
        repository.addHistoryItem(newItem)

        assertEquals(currentHistory.items + newItem, state.history.items)
    }

    @Test
    fun testDeleteHistoryItem() {
        val itemToDelete = HistoryItem("item1", emptyList())
        val currentHistory = History(listOf(itemToDelete, HistoryItem("item2", emptyList())))
        state.history = currentHistory

        repository.deleteHistoryItem(itemToDelete)
        assertEquals(currentHistory.items.filterNot { it == itemToDelete }, state.history.items)
    }

    @Test
    fun testRenameHistoryItem() {
        val itemToRename = HistoryItem("item1", emptyList())
        val currentHistory = History(listOf(itemToRename, HistoryItem("item2", emptyList())))
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
        val currentHistory = History(listOf(HistoryItem("item1", emptyList()), HistoryItem("item2", emptyList())))
        state.history = currentHistory
        val result = repository.getAllHistoryItems()
        assertEquals(currentHistory.items, result)
    }
}
