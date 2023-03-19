package com.github.jcraane.gptmentorplugin.ui.history

import com.github.jcraane.gptmentorplugin.ui.history.state.HistoryItem
import com.github.jcraane.gptmentorplugin.ui.history.state.PluginStateHistoryRepository
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import java.awt.BorderLayout
import java.awt.Component
import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

//    todo double click should load chat with chat history
//                    todo how to communicate to chat panel to load new chat?
class HistoryPanel(
    onChatSelected: (HistoryItem) -> Unit,
) : JPanel(), HistoryView {
    val presenter = HistoryPresenter(this, PluginStateHistoryRepository())

    private val historyList = JBList<HistoryItem>().apply {
        cellRenderer = HistoryItemRenderer()
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if (e == null) {
                    return
                }

                if (e.clickCount == 2) {
                    val selected = selectedValue
                    onChatSelected(selected)
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    val selectedIndices = selectedIndices
                    if (selectedIndices.size == 1) {
                        val popupMenu = addPopupMenuWithDeleteAction()

                        val renameItem = JMenuItem("Rename")
                        renameItem.addActionListener {
                            showEditTextField(selectedIndices)
                        }
                        popupMenu.add(renameItem)

                        popupMenu.show(e.component, e.x, e.y)
                    } else if (selectedIndices.isNotEmpty()) {
                        val popupMenu = addPopupMenuWithDeleteAction()
                        popupMenu.show(e.component, e.x, e.y)
                    }
                }
            }
        })
    }

    private fun JBList<HistoryItem>.addPopupMenuWithDeleteAction(): JPopupMenu {
        val popupMenu = JPopupMenu()
        val deleteItem = JMenuItem("Delete")
        deleteItem.addActionListener {
            val selectedValues = selectedValues
            presenter.deleteAll(selectedValues)
        }
        popupMenu.add(deleteItem)
        return popupMenu
    }

    private fun JBList<HistoryItem>.showEditTextField(selectedIndices: IntArray) {
        val selectedBounds = getCellBounds(selectedIndices.first(), selectedIndices.first())
        val selectedItem = selectedValue
        val textField = JTextField(selectedItem.title)
        textField.bounds = selectedBounds
        add(textField)
        textField.requestFocusInWindow()
        textField.caretPosition = textField.text.length
        repaint()

        textField.addActionListener {
            val newValue = textField.text
            presenter.rename(selectedItem, newValue)
            remove(textField)
            repaint()
        }

        textField.addFocusListener(object : FocusAdapter() {
            override fun focusLost(e: FocusEvent?) {
                remove(textField)
                repaint()
            }
        })
    }

    init {
        layout = BorderLayout()
        add(historyList, BorderLayout.CENTER)
        presenter.refreshHistory()
    }

    override fun showHistory(history: List<HistoryItem>) {
        historyList.setListData(history.toTypedArray())
    }

    private class HistoryItemRenderer : JBLabel(), ListCellRenderer<HistoryItem> {
        init {
            isOpaque = true
        }

        override fun getListCellRendererComponent(
            list: JList<out HistoryItem>?,
            value: HistoryItem?,
            index: Int,
            isSelected: Boolean,
            cellHasFocus: Boolean,
        ): Component {
            text = value?.title // use the title property to display the name
            background = if (isSelected) UIManager.getColor("List.selectionBackground") else UIManager.getColor("List.background")
            foreground = if (isSelected) UIManager.getColor("List.selectionForeground") else UIManager.getColor("List.foreground")
            return this
        }
    }
}
