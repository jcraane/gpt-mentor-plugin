package com.github.jcraane.gptmentorplugin.ui.history

import com.github.jcraane.gptmentorplugin.ui.history.state.HistoryItem
import com.github.jcraane.gptmentorplugin.ui.history.state.PluginStateHistoryRepository
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBList
import java.awt.BorderLayout
import java.awt.Component
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.*

class HistoryPanel : JPanel(), HistoryView {
    val presenter = HistoryPresenter(this, PluginStateHistoryRepository())

    private val historyList = JBList<HistoryItem>().apply {
        cellRenderer = HistoryItemRenderer()
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent?) {
                if (e == null) {
                    return
                }

                if (e.clickCount == 2) {
                    //    todo double click should load chat with chat history
//                    todo how to communicate to chat panel to load new chat?
                    val selected = selectedValue
                    JOptionPane.showMessageDialog(this@apply, "You double-clicked on $selected")
                } else if (SwingUtilities.isRightMouseButton(e)) {
                    val index = locationToIndex(e.point)
                    selectedIndex = index
                    val popupMenu = JPopupMenu()
                    /*val renameItem = JMenuItem("Rename")
                    renameItem.addActionListener {
                        val selectedItem = selectedValue
                    }*/
                    val deleteItem = JMenuItem("Delete")
                    deleteItem.addActionListener {
                        val selectedItem = selectedValue
                        presenter.delete(selectedItem)
                    }
//                    popupMenu.add(renameItem)
                    popupMenu.add(deleteItem)
                    popupMenu.show(e?.component, e.x, e.y)
                }
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
